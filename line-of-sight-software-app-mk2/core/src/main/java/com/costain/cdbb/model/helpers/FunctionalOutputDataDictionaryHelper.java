/*
 * Copyright © 2022 Costain Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.model.helpers;


import com.costain.cdbb.core.CdbbValidationError;
import com.costain.cdbb.model.FunctionalOutputDataDictionary;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.opencsv.CSVParser;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalOutputDataDictionaryHelper {

    @Autowired
    FunctionalOutputDataDictionaryRepository foDataDictionaryRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository foDataDictionaryEntryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CompressionHelper compressionHelper;

    public FunctionalOutputDataDictionary fromDao(FunctionalOutputDataDictionaryDAO dao) {
        FunctionalOutputDataDictionary dto = new FunctionalOutputDataDictionary();
        dto.id(dao.getId());
        dto.name(dao.getName());
        return dto;
    }

    public FunctionalOutputDataDictionaryDAO fromDto(FunctionalOutputDataDictionary foDataDictionary) {
        return fromDto(FunctionalOutputDataDictionaryDAO.builder(), foDataDictionary.getName());
    }

    public FunctionalOutputDataDictionaryDAO fromDto(UUID id, FunctionalOutputDataDictionary foDataDictionary) {
        return fromDto(FunctionalOutputDataDictionaryDAO.builder().id(id), foDataDictionary.getName());
    }

    private FunctionalOutputDataDictionaryDAO fromDto(
        FunctionalOutputDataDictionaryDAO.FunctionalOutputDataDictionaryDAOBuilder builder, String name) {
        return builder.name(name).build();
    }

    public FunctionalOutputDataDictionaryDAO importDictionary(String base64CompressedFoData) {
        // first line is name of dictionary
        List<String> importedRecords = null;
        String errorMsg = null;
        try {
            importedRecords =
                List.of(compressionHelper.decompress(Base64.getDecoder().decode(base64CompressedFoData))
                    .split("\\n"));
        } catch (IOException e) {
            errorMsg = "Invalid input format in Functional Output Data Dictionary - Import aborted";
        }
        if (null == errorMsg) {
            final var ref = new Object() {
                FunctionalOutputDataDictionaryDAO foDataDictionary = null;
            };
            final CSVParser parser = new CSVParser();
            List<String> finalImportedRecords = importedRecords;
            transactionTemplate.execute(transaction -> {
                for (int i = 0; i < finalImportedRecords.size(); i++) {
                    if (i == 0) {
                        String name = finalImportedRecords.get(0);
                        if (null != foDataDictionaryRepository.findByName(name)) {
                            throw new CdbbValidationError(
                                "A functional output data dictionary already exists with the name '" + name + "'");
                        }
                        ref.foDataDictionary =
                            foDataDictionaryRepository
                                .save(FunctionalOutputDataDictionaryDAO.builder().name(name).build());
                    } else {
                        String[] record;
                        try {
                            record = parser.parseLine(finalImportedRecords.get(i));

                        } catch (IOException e) {
                            throw new CdbbValidationError(
                                "Invalid line (#" + (i + 1) + ") in Functional Output Dictionary '"
                                    + finalImportedRecords.get(i)
                                    + " - Import aborted");
                        }
                        if (record.length != 2) {
                            throw new CdbbValidationError(
                                "Invalid line (#" + (i + 1) + ") in Functional Output Data Dictionary '"
                                    + finalImportedRecords.get(i)
                                    + " - Import aborted");
                        }
                        foDataDictionaryEntryRepository.save(FunctionalOutputDataDictionaryEntryDAO.builder()
                            .foDictionaryId(ref.foDataDictionary.getId()).entryId(record[0]).text(record[1]).build());
                    }
                }
                return 0;
            });
            return ref.foDataDictionary;
        }
        throw new CdbbValidationError(errorMsg);
    }
}

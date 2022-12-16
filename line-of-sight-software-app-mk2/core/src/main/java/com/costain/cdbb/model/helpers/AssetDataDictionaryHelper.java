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
import com.costain.cdbb.model.AssetDataDictionary;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
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

/**
 * Provides helper functions for managing and manipulating asset data dictionaries.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetDataDictionaryHelper {

    @Autowired
    AssetDataDictionaryRepository assetDataDictionaryRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDataDictionaryEntryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CompressionHelper compressionHelper;


    /**
     * Create an asset dto from an asset dao.
     * @param dao the source data
     * @return AssetDataDictionary the created dto.
     */

    public AssetDataDictionary fromDao(AssetDataDictionaryDAO dao) {
        AssetDataDictionary dto = new AssetDataDictionary();
        dto.id(dao.getId());
        dto.name(dao.getName());
        return dto;
    }

    /**
     * Create AssetDataDictionaryDAO for a new asset from an asset dto.
     * @param assetDataDictionary the source data
     * @return AssetDataDictionaryDAO the created dao
     */
    public AssetDataDictionaryDAO fromDto(AssetDataDictionary assetDataDictionary) {
        return fromDto(AssetDataDictionaryDAO.builder(), assetDataDictionary.getName());
    }

    /**
     * Create AssetDataDictionaryDAO for an existing asset from an asset dto.
     * @param assetDdId the asset dictionary id
     * @param assetDataDictionary the source data
     * @return AssetDataDictionaryDAO the created dao
     */
    public AssetDataDictionaryDAO fromDto(UUID assetDdId, AssetDataDictionary assetDataDictionary) {
        return fromDto(AssetDataDictionaryDAO.builder().id(assetDdId), assetDataDictionary.getName());
    }

    private AssetDataDictionaryDAO fromDto(AssetDataDictionaryDAO.AssetDataDictionaryDAOBuilder builder, String name) {
        return builder.name(name).build();
    }

    /**
     * Import  AssetDataDictionaryDAO for set of dictionary entries uploaded from client.
     * @param base64CompressedAssetData <p>base64 compressed string.
     *                        First line is name of dictionary (must be unique) the rest are CSV key value pairs</p>
     * @return AssetDataDictionaryDAO the created dao
     */
    public AssetDataDictionaryDAO importDictionary(String base64CompressedAssetData) {
        List<String> importedRecords = null;
        String errorMsg = null;
        try {
            importedRecords =
                List.of(compressionHelper.decompress(Base64.getDecoder().decode(base64CompressedAssetData))
            .split("\\n"));
        } catch (IOException e) {
            errorMsg = "Invalid input format in Asset Data Dictionary - Import aborted";
        }
        if (null == errorMsg) {
            final var ref = new Object() {
                AssetDataDictionaryDAO assetDataDictionary = null;
            };
            final CSVParser parser = new CSVParser();
            List<String> finalImportedRecords = importedRecords;
            transactionTemplate.execute(transaction -> {
                for (int i = 0; i < finalImportedRecords.size(); i++) {
                    if (i == 0) {
                        String name = finalImportedRecords.get(0);
                        if (null != assetDataDictionaryRepository.findByName(name)) {
                            throw new CdbbValidationError(
                                "An asset data dictionary already exists with the name '" + name + "'");
                        }
                        ref.assetDataDictionary =
                            assetDataDictionaryRepository
                                .save(AssetDataDictionaryDAO.builder().name(name).build());
                    } else {
                        String[] record;
                        try {
                            record = parser.parseLine(finalImportedRecords.get(i));
                        } catch (IOException e) {
                            throw new CdbbValidationError(
                                "Invalid line (#" + (i + 1) + ") in Asset Data Dictionary '"
                                    + finalImportedRecords.get(i)
                                    + " - Import aborted");
                        }
                        if (record.length != 2) {
                            throw new CdbbValidationError(
                                "Invalid line (#" + (i + 1) + ") in Asset Data Dictionary '"
                                    + finalImportedRecords.get(i)
                                    + " - Import aborted");
                        }
                        assetDataDictionaryEntryRepository.save(AssetDataDictionaryEntryDAO.builder()
                            .assetDictionaryId(ref.assetDataDictionary.getId())
                            .entryId(record[0]).text(record[1]).build());
                    }
                }
                return 0;
            });
            return ref.assetDataDictionary;
        }
        throw new CdbbValidationError(errorMsg);
    }
}

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
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.FunctionalOutput;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputWithId;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import com.opencsv.CSVParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalOutputHelper {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository ddeRepository;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CompressionHelper compressionHelper;

    public FunctionalOutputWithId fromDao(FunctionalOutputDAO dao) {
        FunctionalOutputWithId dto = new FunctionalOutputWithId();
        dto.id(dao.getId());
        FunctionalOutputDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new DataDictionaryEntry()
            .entryId(dde.getEntryId())
            .text(dde.getEntryId() + "-" + dde.getText()));
        dto.firs(dao.getFirs() != null ? dao.getFirs().stream().toList() : Collections.emptyList());
        dto.assets(dao.getAssets() != null ? dao.getAssets().stream().map(AssetDAO::getId).toList()
            : Collections.emptyList());
        return dto;
    }

    public FunctionalOutputDAO inputFromDto(UUID projectId, UUID foId, FunctionalOutput fo) {
        return fromDto(FunctionalOutputDAO.builder().id(foId),
            projectId,
            fo.getDataDictionaryEntry().getEntryId(),
            fo.getFirs(),
            fo.getAssets());
    }

    public FunctionalOutputDAO fromDto(FunctionalOutputWithId functionalOutput, UUID projectId) {
        return fromDto(FunctionalOutputDAO.builder(),
            projectId,
            functionalOutput.getDataDictionaryEntry().getEntryId(),
            functionalOutput.getFirs(),
            functionalOutput.getAssets());
    }

    public FunctionalOutputDAO fromDto(UUID id, FunctionalOutputWithId functionalOutput, UUID projectId) {
        return fromDto(FunctionalOutputDAO.builder().id(id),
            projectId,
            functionalOutput.getDataDictionaryEntry().getEntryId(),
            functionalOutput.getFirs(), functionalOutput.getAssets());
    }

    private FunctionalOutputDAO fromDto(FunctionalOutputDAO.FunctionalOutputDAOBuilder builder,
                                        UUID projectId,
                                        String ddeId, Collection<String> firs, Collection<UUID> assets) {
        ProjectDAO projectDao = projectRepository.findById(projectId).get();
        FunctionalOutputDataDictionaryEntryDAO optFoDdeDao =
            ddeRepository.findByFoDictionaryIdAndEntryId(projectDao.getFoDataDictionary().getId(), ddeId);
        return builder
            .projectId(projectId)
            .dataDictionaryEntry(optFoDdeDao)
            .firs(new HashSet<>(firs))
            .assets(assets.stream().map(id -> AssetDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
    }


    public List<FunctionalOutputDAO> importFirs(String base64CompressedFirsData, UUID projectId) {
        List<String> importedRecords = null;
        String errorMsg = null;
        try {
            importedRecords =
                List.of(compressionHelper.decompress(Base64.getDecoder().decode(base64CompressedFirsData))
                    .split("\\n"));
        } catch (IOException e) {
            errorMsg = "Invalid input format for FIRs data - Import aborted";
        }
        if (null == errorMsg) {
            Map<String, FunctionalOutputDAO> foDaosMap = new HashMap<>(importedRecords.size());
            final CSVParser parser = new CSVParser();
            List<String> finalImportedRecords = importedRecords;
            transactionTemplate.execute(transaction -> {
                int lineNo = 0;
                for (String record : finalImportedRecords) {
                    lineNo++;
                    String[] fields;
                    try {
                        fields = parser.parseLine(record);
                    } catch (IOException e) {
                        throw new CdbbValidationError(
                            "Invalid line (#" + lineNo + ") in FIRs import '" + record
                                + " - Import aborted");
                    }
                    FunctionalOutputDAO foDao =
                        foRepository.findByProjectIdAndDataDictionaryEntry_EntryId(projectId, fields[0]);
                    if (foDao == null) {
                        // new fo required for this project
                        ProjectDAO projectDao = projectRepository.findById(projectId).get();
                        FunctionalOutputDataDictionaryEntryDAO foDdeDao =
                            ddeRepository.findByFoDictionaryIdAndEntryId(
                                projectDao.getFoDataDictionary().getId(), fields[0]);
                        if (null == foDdeDao) {
                            throw new CdbbValidationError(
                                "Unknown key in file = '" + fields[0]
                                    + "' on line #" + lineNo + " - Import of FIRs aborted");
                        }
                        foDao = FunctionalOutputDAO.builder()
                            .projectId(projectId)
                            .dataDictionaryEntry(foDdeDao)
                            .firs(new HashSet<>())
                            .build();
                    }
                    foDao.getFirs().add(fields[1]);
                    foDaosMap.put(fields[0], foRepository.save(foDao));
                }
                return 0;
            });
            List<FunctionalOutputDAO> foDaos = new ArrayList<>(foDaosMap.size());
            for (Map.Entry<String, FunctionalOutputDAO> entry : foDaosMap.entrySet()) {
                foDaos.add(entry.getValue());
            }
            return foDaos;
        }
        throw new CdbbValidationError(errorMsg);
    }
}

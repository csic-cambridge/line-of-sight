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
import com.costain.cdbb.model.Asset;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.AssetWithId;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Provides helper functions for managing and manipulating assets.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetHelper {
    @Autowired
    AssetDataDictionaryEntryRepository ddeRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    ProjectRepository projectRepository;


    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CompressionHelper compressionHelper;

    /**
     * Create a dto from an asset dao.
     * @param dao the source data
     * @return AssetWithId the created dto
     */

    public AssetWithId fromDao(AssetDAO dao) {
        AssetWithId dto = new AssetWithId();
        dto.id(dao.getId());
        AssetDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new DataDictionaryEntry()
            .entryId(dde.getEntryId())
            .text(dde.getEntryId() + "-" + dde.getText())
        );
        dto.airs(dao.getAirs() != null ? dao.getAirs().stream().toList() : Collections.emptyList());
        return dto;
    }

    /**
     * Create an asset dao for an existing asset from an asset dto.
     * @param id the id of the asset
     * @param asset the source asset data
     * @param projectId the project the asset belongs to
     * @return AssetDAO the created asset dao
     */
    public AssetDAO fromDto(UUID id, Asset asset, UUID projectId) {
        return fromDto(AssetDAO.builder().id(id), projectId,
            asset.getDataDictionaryEntry().getEntryId(), asset.getAirs());
    }

    /**
     * Create an asset dao for a new asset from an asset dto.
     * @param asset the source asset data
     * @param projectId the project the asset belongs to
     * @return AssetDAO the created asset dao
     */
    public AssetDAO fromDto(AssetWithId asset, UUID projectId) {
        return fromDto(AssetDAO.builder(), projectId,
            asset.getDataDictionaryEntry().getEntryId(), asset.getAirs());
    }

    private AssetDAO fromDto(AssetDAO.AssetDAOBuilder builder, UUID projectId,
                             String ddeEntryId,
                             Collection<String> airs) {
        ProjectDAO projectDao = projectRepository.findById(projectId).get();
        AssetDataDictionaryEntryDAO assetDdeDao =
            ddeRepository.findByAssetDictionaryIdAndEntryId(projectDao.getAssetDataDictionary().getId(), ddeEntryId);
        return builder
            .projectId(projectId)
            .dataDictionaryEntry(assetDdeDao)
            .airs(airs != null ? new HashSet<>(airs) : Collections.emptySet())
            .build();
    }

    public List<AssetDAO> importAirs(String base64CompressedAirsData, UUID projectId) {
        List<String> importedRecords = null;
        String errorMsg = null;
        try {
            importedRecords =
                List.of(compressionHelper.decompress(Base64.getDecoder().decode(base64CompressedAirsData))
                    .split("\\n"));
        } catch (IOException e) {
            errorMsg = "Invalid input format for AIRs data - Import aborted";
        }
        if (null == errorMsg) {
            Map<String, AssetDAO> assetDaosMap = new HashMap<>(importedRecords.size());
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
                    AssetDAO assetDao =
                        assetRepository.findByProjectIdAndDataDictionaryEntry_EntryId(projectId, fields[0]);
                    if (assetDao == null) {
                        // new asset required for this project
                        ProjectDAO projectDao = projectRepository.findById(projectId).get();
                        AssetDataDictionaryEntryDAO assetDdeDao =
                            ddeRepository.findByAssetDictionaryIdAndEntryId(
                                projectDao.getAssetDataDictionary().getId(), fields[0]);
                        if (null == assetDdeDao) {
                            throw new CdbbValidationError(
                                "Unknown key in file = '" + fields[0]
                                    + "' on line #" + lineNo + " - Import of AIRs aborted");
                        }
                        assetDao = AssetDAO.builder()
                            .projectId(projectId)
                            .dataDictionaryEntry(assetDdeDao)
                            .airs(new HashSet<>())
                            .build();
                    }
                    assetDao.getAirs().add(fields[1]);
                    assetDaosMap.put(fields[0], assetRepository.save(assetDao));
                }
                return 0;
            });
            List<AssetDAO> assetDaos = new ArrayList<>(assetDaosMap.size());
            for (Map.Entry<String, AssetDAO> entry : assetDaosMap.entrySet()) {
                assetDaos.add(entry.getValue());
            }
            return assetDaos;
        }
        throw new CdbbValidationError(errorMsg);
    }
}

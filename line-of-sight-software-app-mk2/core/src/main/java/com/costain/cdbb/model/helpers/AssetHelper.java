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
import com.costain.cdbb.core.events.ClientNotification;
import com.costain.cdbb.core.events.EventType;
import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AirsWithLinkedOirs;
import com.costain.cdbb.model.Asset;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.AssetWithId;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.Oir;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.AirsRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Provides helper functions for managing and manipulating assets.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetHelper {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    AssetDataDictionaryEntryRepository ddeRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    AirsRepository airsRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
        if (dao.getAirs() != null) {
            //ArrayList<AirsDAO> sortedAirs = new ArrayList<>(dao.getAirs());
            logger.warn("********** Not sorting AIRS**************************************");
            //Collections.sort(sortedAirs, String.CASE_INSENSITIVE_ORDER);
            List<AirsWithLinkedOirs> airs = new ArrayList<>(dao.getAirs().size());
            for (AirsDAO airsDao: dao.getAirs()) {
                List<Oir> oirs = new ArrayList<>();
                if (null != airsDao.getOirs()) {
                    for (OirDAO oirDao : airsDao.getOirs()) {
                        oirs.add(new Oir().id(oirDao.getId()).oir(oirDao.getOirs()));
                    }
                }
                airs.add(new AirsWithLinkedOirs().id(airsDao.getId().toString())
                    .airs(airsDao.getAirs())
                    .oirs(oirs));
            }
            dto.airs(airs);
        } else {
            dto.airs(Collections.emptyList());
        }
        return dto;
    }

    /**
     * Update an asset dao for an existing asset from an asset dto.
     * @param assetId the id of the asset
     * @param asset the source asset data
     * @param projectId the project the asset belongs to
     * @return AssetDAO the created asset dao
     */
    public AssetDAO fromDto(UUID assetId, Asset asset, UUID projectId) {
        notifyClientOfProjectChange(projectId);
        return fromDto(AssetDAO.builder().id(assetId), projectId,
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
                             List<AirsWithLinkedOirs> airs) {
        ProjectDAO projectDao = projectRepository.findById(projectId).get();
        AssetDataDictionaryEntryDAO assetDdeDao =
            ddeRepository.findByAssetDictionaryIdAndEntryId(projectDao.getAssetDataDictionary().getId(), ddeEntryId);

        AssetDAO dao =  builder
            .projectId(projectId)
            .dataDictionaryEntry(assetDdeDao)
            .airs(this.makeAirsDaoSetFromAirsList(airs))
            .build();
        //dao.setAirs(this.makeAirsSetFromAirs(airs));
        notifyClientOfProjectChange(projectId);
        return dao;
    }

    private Set<AirsDAO> makeAirsDaoSetFromAirsList(Collection<AirsWithLinkedOirs> airs) {
        if (null == airs) {
            return Collections.emptySet();
        }
        Set<AirsDAO> result = new HashSet<>(airs.size());
        for (AirsWithLinkedOirs air: airs) {
            AirsDAO airsDao = AirsDAO.builder()
                .id(air.getId()) //AirsDAO.isNewAirsDao(air.getId()) ? null : UUID.fromString(air.getId()))
                .airs(air.getAirs()).build();
            result.add(airsDao);
        }
        return result;
    }
    /**
     * Import AIRs uploaded from client.
     * @param base64CompressedAirsData <p>base64 compressed string.
     *                        CSV key value pairs of Asset Id and AIR text</p>
     * @param projectId project Assets/AIRs to be imported into
     * @return List&lt;AssetDAO&gt; the assets imported
     */

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
                            "Invalid line (#" + lineNo + ") in AIRs import '" + record
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
                    // do not add if air text already in this asset
                    if (null == airsRepository.findByAssetDaoIdAndAirs(assetDao.getId(), fields[1])) {
                        AirsDAO airsDao = AirsDAO.builder().airs(fields[1]).build();
                        assetDao.getAirs().add(airsDao);
                        assetDao.setAirs();
                    }
                    assetDaosMap.put(fields[0], assetRepository.save(assetDao));
                }
                notifyClientOfProjectChange(projectId);
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

    /**
     *  Get all AIRs in alphabetical order, de-duplicated.
     *  NOTE: see https://docs.spring.io/spring-framework/docs/current/reference/html
     *  /web-reactive.html#webflux-codecs-jackson why this is necessary to encode here
     *  @return Airs All AIRs sorted alphabetically
     */
    public List<AirsDAO> findAllAirs() throws JsonProcessingException {
        logger.warn("*************NOT RETURNING AIRS IN ORDER");
        List<AirsDAO> airs = new ArrayList<>();
        assetRepository.findAll().forEach(
            foDao -> {
                foDao.getAirs().forEach(air -> airs.add(air));
            });
        return airs;
        /*Set<String> set = new TreeSet<String>();
        set.addAll(ref.airs);
        ref.airs = new ArrayList<String>(set);
        Collections.sort(ref.airs, String.CASE_INSENSITIVE_ORDER);
        return new ObjectMapper().writeValueAsString(ref.airs);*/
    }

    /**
     * Deletes an asset from a project.
     * @param projectId project id of project asset belongs to
     * @param assetId id of asset to delete
     */
    public void deleteAsset(UUID projectId, UUID assetId) {
        assetRepository.deleteById(assetId);
        notifyClientOfProjectChange(projectId);
    }

    private void notifyClientOfProjectChange(UUID projectId) {
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED,
                null, projectId)));
    }
}

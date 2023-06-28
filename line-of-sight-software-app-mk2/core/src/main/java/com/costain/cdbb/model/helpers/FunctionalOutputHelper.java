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
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.Firs;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.model.FunctionalOutput;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputWithId;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.FirsRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
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
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Provides helper functions for managing and manipulating functional outputs.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalOutputHelper {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository ddeRepository;

    @Autowired
    FirsRepository firsRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CompressionHelper compressionHelper;

    /**
     * Create a dto from an asset dao.
     * @param dao the source data
     * @return FunctionalOutputWithId the created dto
     */
    public FunctionalOutputWithId fromDao(FunctionalOutputDAO dao) {
        FunctionalOutputWithId dto = new FunctionalOutputWithId();
        dto.id(dao.getId());
        FunctionalOutputDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new DataDictionaryEntry()
            .entryId(dde.getEntryId())
            .text(dde.getEntryId() + "-" + dde.getText()));
        if (dao.getFirs() != null) {
            //ArrayList<FirsDAO> sortedFirs = new ArrayList<>(dao.getFirs());
            logger.warn("********** Not sorting AIRS**************************************");
            //Collections.sort(sortedFirs, String.CASE_INSENSITIVE_ORDER);
            List<Firs> firs = new ArrayList<>(dao.getFirs().size());
            for (FirsDAO firsDao: dao.getFirs()) {
                firs.add(new Firs().id(firsDao.getId().toString()).firs(firsDao.getFirs()));
            }
            dto.firs(firs);
        } else {
            dto.firs(Collections.emptyList());
        }
        dto.assets(dao.getAssets() != null ? dao.getAssets().stream().map(AssetDAO::getId).toList()
            : Collections.emptyList());
        return dto;
    }

    /**
     * Create an asset dao for an existing asset from an asset dto.
     * @param projectId the project the asset belongs to
     * @param foId the id of the functional ouput
     * @param fo the source functional output data
     * @return FunctionalOutputDAO the created functional output dao
     */
    public FunctionalOutputDAO inputFromDto(UUID projectId, UUID foId, FunctionalOutput fo) {
        return fromDto(FunctionalOutputDAO.builder().id(foId),
            projectId,
            fo.getDataDictionaryEntry().getEntryId(),
            fo.getFirs(),
            fo.getAssets());
    }

    /**
     * Create a functional output dao for a new functional output from a functional output dto.
     * @param functionalOutput the functional output asset data
     * @param projectId the project the functional output belongs to
     * @return FunctionalOutputDAO the created functional output dao
     */
    public FunctionalOutputDAO fromDto(FunctionalOutputWithId functionalOutput, UUID projectId) {
        notifyClientOfProjectChange(projectId);
        return fromDto(FunctionalOutputDAO.builder(),
            projectId,
            functionalOutput.getDataDictionaryEntry().getEntryId(),
            functionalOutput.getFirs(),
            functionalOutput.getAssets());
    }

    private FunctionalOutputDAO fromDto(FunctionalOutputDAO.FunctionalOutputDAOBuilder builder,
                                        UUID projectId,
                                        String ddeId, List<Firs> firs, Collection<UUID> assets) {
        ProjectDAO projectDao = projectRepository.findById(projectId).get();
        FunctionalOutputDataDictionaryEntryDAO optFoDdeDao =
            ddeRepository.findByFoDictionaryIdAndEntryId(projectDao.getFoDataDictionary().getId(), ddeId);
        FunctionalOutputDAO dao = builder
            .projectId(projectId)
            .dataDictionaryEntry(optFoDdeDao)
            .firs(makeFirsSetFromFirs(firs))
            .assets(assets.stream().map(id -> AssetDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
        notifyClientOfProjectChange(projectId);
        return dao;
    }

    private Set<FirsDAO> makeFirsSetFromFirs(Collection<Firs> firs) {
        if (null == firs) {
            return Collections.emptySet();
        }
        Set<FirsDAO> result = new HashSet<>(firs.size());
        for (Firs fir: firs) {
            FirsDAO firsDao =  FirsDAO.builder()
                .id(fir.getId())
                .firs(fir.getFirs()).build();
            result.add(firsDao);
        }
        return result;
    }

    /**
     * Import FIRs uploaded from client.
     * @param base64CompressedFirsData <p>base64 compressed string.
     *                        CSV key value pairs of FO Id and FIR text</p>
     * @param projectId project Functional Outputs/FIRs to be imported into
     * @return List&lt;FunctionalOutputDAO&gt; the assets imported
     */
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
                    // do not add if fir text already in this fo
                    if (null == firsRepository.findByFoDaoIdAndFirs(foDao.getId(), fields[1])) {
                        FirsDAO firsDao = FirsDAO.builder().firs(fields[1]).build();
                        foDao.getFirs().add(firsDao);
                        foDao.setFirs();
                    }
                    foDaosMap.put(fields[0], foRepository.save(foDao));
                }
                notifyClientOfProjectChange(projectId);
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

    /**
     *  Get all FIRs in alphabetical order,  de-duplicated.
     *  NOTE: see https://docs.spring.io/spring-framework/docs/current/reference/html
     *  /web-reactive.html#webflux-codecs-jackson why this is necessary to encode here
     * @return Firs All FIRs sorted alphabetically
     */
    public List<FirsDAO>  findAllFirs() throws JsonProcessingException {
        logger.warn("*************NOT RETURNING FIRS IN ORDER");
        List<FirsDAO> firs = new ArrayList<>();
        foRepository.findAll().forEach(
            foDao -> {
                foDao.getFirs().forEach(fir -> firs.add(fir));
            });
        return firs;
        /*Set<String> set = new TreeSet<String>();
        set.addAll(ref.firs);
        ref.firs = new ArrayList<String>(set);
        Collections.sort(ref.firs, String.CASE_INSENSITIVE_ORDER);
        return new ObjectMapper().writeValueAsString(ref.firs);*/
    }

    /**
     * Deletes a functional output.
     * @param functionalOutputId id of functional output to delete
     */
    public void deleteFo(UUID projectId, UUID functionalOutputId) {
        foRepository.deleteById(functionalOutputId);
        notifyClientOfProjectChange(projectId);
    }

    private void notifyClientOfProjectChange(UUID projectId) {
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED,
                null, projectId)));
    }
}

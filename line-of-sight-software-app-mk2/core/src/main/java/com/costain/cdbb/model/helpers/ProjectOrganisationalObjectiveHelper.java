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

import com.costain.cdbb.core.events.ClientNotification;
import com.costain.cdbb.core.events.EventType;
import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.model.Airs;
import com.costain.cdbb.model.DeletedOirDAO;
import com.costain.cdbb.model.DeletedOirPk;
import com.costain.cdbb.model.Oir;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OirsWithLinkedAirs;
import com.costain.cdbb.model.OoVersion;
import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveUpdate;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveWithId;
import com.costain.cdbb.repositories.DeletedOirRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OirRepository;
import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides helper functions for managing and manipulating project organisational objectives (POOs).
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProjectOrganisationalObjectiveHelper {

    @Autowired
    OoVersionRepository oovRepository;

    @Autowired
    OirRepository oirRepository;

    @Autowired
    DeletedOirRepository deletedOirRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * Delete a project organisational objective by its id.
     * @param projectId The project id the POO belongs to
     * @param pooId The id of the POO
     */
    public void deleteById(UUID projectId, UUID pooId) {
        pooRepository.deleteById(pooId);
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED,
                null, projectId)));
    }

    /**
     * Get ProjectOrganisationalObjectiveDAO from POO dto.
     * @param poo dto
     * @return ProjectOrganisationalObjectiveDAO poo dao
     */
    public ProjectOrganisationalObjectiveDAO fromDto(ProjectOrganisationalObjectiveUpdate poo) {
        // if values are null then use existing else update
        Optional<ProjectOrganisationalObjectiveDAO> optDao = pooRepository.findById(poo.getId());
        if (poo.getFrs() != null) {
            optDao.get().setFrs(poo.getFrs().stream().map(
                uuid -> frRepository.findById(uuid).get()).collect(Collectors.toSet()));
        }
        if (poo.getOoVersionId() != null) {
            optDao.get().setOoVersion(oovRepository.findById(poo.getOoVersionId()).get());
        }
        updateOirs(poo.getOirIds(), optDao.get());
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED,
                null, optDao.get().getProjectId())));
        return optDao.get();
    }

    /**
     * Create a dto from an POO dao.
     * @param dao the source data
     * @return ProjectOrganisationalObjectiveWithId the created dto
     */
    public ProjectOrganisationalObjectiveWithId fromDao(ProjectOrganisationalObjectiveDAO dao) {
        // need to get list of ooVersions (current project to latest oo) from db
        List<OoVersionDAO> ooVersionDaos = oovRepository.findNonDiscardedByPoo(dao.getId().toString());
        List<OoVersion> ooVersions = ooVersionDaos.stream().map(oovDao -> {
            OoVersion ooVersion = new OoVersion();
            ooVersion.id(oovDao.getId());
            ooVersion.dateCreated(oovDao.getDateCreated().getTime());
            ooVersion.name(oovDao.getName());
            ooVersion.ooId(oovDao.getOo().getId());
            return ooVersion;
        }).toList();
        List<OirDAO> oirs = new ArrayList<>();
        List<OirDAO> deletedOirs = new ArrayList<>();
        if (dao.getOoVersion() != null && dao.getOoVersion().getOo().getOirDaos() != null) {
            List<String> deletedOirIds = new ArrayList<>();
            oirRepository.findDeletedOirsForProjectAndOo(
                dao.getProjectId().toString(), dao.getOoVersion().getOo().getId().toString())
                    .forEach(deletedOirDao  ->  {
                        deletedOirs.add(deletedOirDao);
                        deletedOirIds.add(deletedOirDao.getId().toString());
                    }
                );
            oirs = new ArrayList<>(dao.getOoVersion().getOo().getOirDaos());
            oirs.removeIf(oir -> deletedOirIds.contains(oir.getId().toString()));
        }
        return new ProjectOrganisationalObjectiveWithId()
            .id(dao.getId())
            .projectId(dao.getProjectId())
            .name(dao.getOoVersion().getName())
            .ooVersionId(dao.getOoVersion().getId())
            .ooIsDeleted(dao.getOoVersion().getOo().isDeleted())
            .ooVersions(ooVersions)
            .oirs(oirs.isEmpty()
                ? Collections.emptyList()
                : oirs.stream().map(oirdao ->
                new OirsWithLinkedAirs()
                    .id(oirdao.getId())
                    .oir(oirdao.getOirs())
                    .airs(oirdao.getAirs().isEmpty()
                        ? Collections.emptyList()
                        : oirdao.getAirs().stream().map(airsDao ->
                        new Airs().id(airsDao.getId().toString()).airs(airsDao.getAirs())).toList())).toList()
               )
            .deletedOirs(deletedOirs.isEmpty()
                ? Collections.emptyList()
                : deletedOirs.stream().map(oirdao -> new Oir().id(oirdao.getId()).oir(oirdao.getOirs())).toList()
            )
            .frs(dao.getFrs() != null ? dao.getFrs().stream().map(fr -> fr.getId()).toList() : Collections.emptyList());
    }

    /**
     * Find all active POOs fro a project.
     * @param projectId id of project
     * @return List&lt;ProjectOrganisationalObjectiveDAO&gt; list of ProjectOrganisationalObjectiveDAOs
     */
    public List<ProjectOrganisationalObjectiveDAO> findByProjectActive(UUID projectId) {
        // active poo is one with either !isDeleted OR firs count > 0
        return pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId).stream()
            .filter(dao -> !dao.getOoVersion().getOo().isDeleted()
                    || dao.getFrs() != null && dao.getFrs().size() > 0).toList();
    }

    private void updateOirs(List<UUID> submittedOirIds, ProjectOrganisationalObjectiveDAO pooDao) {
        // submittedOirIds may include previously deleted ones
        List<String> ooOirIds = new ArrayList<>();
        ooOirIds.addAll(pooDao.getOoVersion().getOo().getOirDaos()
            .stream().map(oirDao -> oirDao.getId().toString()).toList());
        // remove all existing deletions from database
        List<OirDAO> doirs = oirRepository.findDeletedOirsForProjectAndOo(pooDao.getProjectId().toString(),
            pooDao.getOoVersion().getOo().getId().toString());
        doirs.forEach(oirDao -> deletedOirRepository.deleteByDeletedOirPk_OirId(oirDao.getId().toString()));
        // remove submitted oirs from list to get remaining deleted ones
        submittedOirIds.forEach(
            oirId -> ooOirIds.remove(oirId.toString())
        );
        // remaining oirs have been deleted
        if (ooOirIds.size() > 0) {
            ooOirIds.forEach(id -> System.out.print(id + " "));
            ooOirIds.forEach(oirId -> {
                    DeletedOirDAO delOirDao = DeletedOirDAO.builder()
                        .deletedOirPk(new DeletedOirPk(oirId, pooDao.getProjectId().toString()))
                        .build();
                    deletedOirRepository.save(delOirDao);
                }
            );
        }
    }
}

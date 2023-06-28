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
import com.costain.cdbb.model.Oir;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.OrganisationalObjective;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.OrganisationalObjectiveWithId;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.OirRepository;
import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Provides helper functions for managing and manipulating Organisational Objectives.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrganisationalObjectiveHelper {
    @Autowired
    OrganisationalObjectiveRepository repository;

    @Autowired
    OoVersionRepository ooVersionRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    OirRepository oirRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /*Mono<String> findMessageByUsername(String username) {
        System.out.println("username = " + username);
        return Mono.just("Hi " + username);
    }*/

    /**
     * Create a dto from an organisational objective dao.
     * @param dao the source data
     * @return OrganisationalObjectiveWithId the created dto
     */
    public OrganisationalObjectiveWithId fromDao(OrganisationalObjectiveDAO dao) {
        OrganisationalObjectiveWithId dto = new OrganisationalObjectiveWithId()
                                                    .id(dao.getId())
                                                    .name(dao.getOoVersions().get(0).getName())
                                                    .isDeleted(dao.isDeleted());

        dto.oirs(dao.getOirDaos() != null
                ? dao.getOirDaos().stream().map(oirdao -> new Oir().id(oirdao.getId()).oir(oirdao.getOirs())).toList()
                : Collections.emptyList());
        return dto;
    }

    /**
     * Mark an Organisational Objective as deleted.
     * @param ooId the id of the organisational objective
     */
    public void markAsDeleted(UUID ooId) {
        Optional<OrganisationalObjectiveDAO> optDao = repository.findById(ooId);
        if (optDao.isPresent()) {
            optDao.get().setIsDeleted(true);
            repository.save(optDao.get());
            applicationEventPublisher.publishEvent(
                new NotifyClientEvent(new ClientNotification(EventType.OOS_CHANGED)));
        }
    }

    /**
     * Create an Organisational Objective DAO from a dto.
     * @param dto the dto
     * @return OrganisationalObjectiveDAO the created organisational objective dao
     */
    public OrganisationalObjectiveDAO fromDto(OrganisationalObjective dto) {
        return fromDto(OrganisationalObjectiveDAO.builder(),
            dto.getName(), null, dto.getOirs(), dto.getIsDeleted());
    }

    /**
     * Update an organisational objective dao for an existing organisational objective from a dto.
     * @param ooId The id of the organisational objectived
     * @param dto the dto
     * @return OrganisationalObjectiveDAO the updated organisation objective dao
     */
    public OrganisationalObjectiveDAO fromDto(UUID ooId, OrganisationalObjective dto) {
        List<OoVersionDAO> ooVersions = ooVersionRepository.findByOo_Id(ooId);
        OrganisationalObjectiveDAO ooDao = fromDto(OrganisationalObjectiveDAO.builder().id(ooId),
            null, ooVersions, dto.getOirs(), dto.getIsDeleted());
        if (!dto.getName().equals(ooVersions.get(0).getOo().getName())) {
            // new version required
            ooDao.addLatestOoVersion(ooVersionRepository.save(
                OoVersionDAO.builder()
                    .name(dto.getName())
                    .oo(ooDao)
                    .dateCreated(new Timestamp(System.currentTimeMillis()))
                    .build()));
            applicationEventPublisher.publishEvent(
                new NotifyClientEvent(new ClientNotification(EventType.OOS_CHANGED)));
        }
        return ooDao;
    }

    private OrganisationalObjectiveDAO fromDto(
        OrganisationalObjectiveDAO.OrganisationalObjectiveDAOBuilder daoBuilder, String name,
        List<OoVersionDAO> ooVersions, Collection<Oir> oirs, boolean isDeleted) {
        Collection<OirDAO> oirDaos = oirs == null
            ? Collections.emptyList()
            : oirs.stream().map(oir -> OirDAO.builder()
            .id(oir.getId())
            .oirs(oir.getOir())
            .build()).toList();

        OrganisationalObjectiveDAO ooDao = daoBuilder
            .name(name)
            .ooVersions(ooVersions)
            .isDeleted(isDeleted)
            .oirDaos(oirDaos)
            .build();

        for (OirDAO oirDao : oirDaos) {
            oirDao.setOoDao(ooDao);
        }
        return ooDao;
    }

    /**
     * Create an organisational objective from a dao.
     * @param dao the organisational objective dao
     * @return OrganisationalObjectiveDAO the saved dao
     */
    public OrganisationalObjectiveDAO createOo(OrganisationalObjectiveDAO dao) {
        OrganisationalObjectiveDAO result = repository.save(dao);
        result.setOoVersion(ooVersionRepository.save(
            OoVersionDAO.builder().name(dao.getName()).oo(result).build()));
        result.getOirDaos().forEach(oirDao -> oirDao.setOoId(result.getId().toString()));
        createPooForAllProjects(result.getOoVersions().get(0));
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.OOS_CHANGED)));
        return result;
    }

    /**
     * Update an organisational objective from a dao.
     * @param dao the organisational objective dao
     * @return OrganisationalObjectiveDAO the saved dao
     */
    public OrganisationalObjectiveDAO updateOo(@NotNull OrganisationalObjectiveDAO dao) {
        List<String> existingOirIds = new ArrayList<>();
        oirRepository.findByOoId(dao.getId().toString())
            .forEach(oirDao -> existingOirIds.add(oirDao.getId().toString()));
        dao.getOirDaos().forEach(oirDao -> {
            if (oirDao.getId() != null) {
                existingOirIds.remove(oirDao.getId().toString());
            }
        });
        existingOirIds.forEach(oirId -> oirRepository.deleteById(UUID.fromString(oirId)));
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.OOS_CHANGED)));
        return repository.save(dao);
    }

    private void createPooForAllProjects(OoVersionDAO ooVersion) {
        List<ProjectDAO> allProjects = (List<ProjectDAO>)projectRepository.findAll();
        for (ProjectDAO projectDao : allProjects) {
            pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                .projectId(projectDao.getId())
                .ooVersion(ooVersion)
                .frs(new HashSet<>())
                .build());
        }
        if (allProjects.size() > 0) {
            applicationEventPublisher.publishEvent(
                new NotifyClientEvent(new ClientNotification(EventType.OOS_CHANGED)));
        }
    }
}

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
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalRequirement;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.FunctionalRequirementWithId;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides helper functions for managing and manipulating Functional Requirements.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalRequirementHelper {

    @Autowired
    private FunctionalRequirementRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void deleteById(UUID projectId, UUID id) {
        repository.deleteById(id);
        notifyClientOfProjectChange(projectId);
    }

    /**
     * Create dto from FunctionalRequirementDAO.
     * @param dao the functional requirements  dao
     * @return FunctionalRequirementWithId the dto
     */
    public FunctionalRequirementWithId fromDao(FunctionalRequirementDAO dao) {
        FunctionalRequirementWithId dto = new FunctionalRequirementWithId();
        dto.id(dao.getId());
        dto.projectId(dao.getProjectId());
        dto.name(dao.getName());
        dto.fos(dao.getFos() != null ? dao.getFos().stream().map(FunctionalOutputDAO::getId).toList()
            : Collections.emptyList());

        return dto;
    }

    /**
     * Create functionalRequirement from dto.
     * @param projectId the project id of the functional requirement
     * @param functionalRequirement the dto
     * @return FunctionalRequirementDAO the functional requirement dto
     */
    public FunctionalRequirementDAO fromDto(UUID projectId, FunctionalRequirement functionalRequirement) {
        return fromDto(FunctionalRequirementDAO.builder(),
            projectId,
            functionalRequirement.getName(),
            functionalRequirement.getFos());
    }

    /**
     * Update Functional Requirement from dto.
     * @param projectId the project id of the functional requirement
     * @param frId the functional requirement id
     * @param functionalRequirement the dto
     * @return FunctionalRequirementDAO the functional requirement dto
     */
    public FunctionalRequirementDAO fromDto(UUID projectId, UUID frId, FunctionalRequirement functionalRequirement) {
        return fromDto(FunctionalRequirementDAO.builder().id(frId),
            projectId,
            functionalRequirement.getName(),
            functionalRequirement.getFos());
    }

    private FunctionalRequirementDAO fromDto(FunctionalRequirementDAO.FunctionalRequirementDAOBuilder builder,
            UUID projectId, String name, Collection<UUID> fos) {
        FunctionalRequirementDAO dao = builder
            .projectId(projectId)
            .name(name)
            .fos(fos.stream().map(id -> FunctionalOutputDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
        notifyClientOfProjectChange(projectId);
        return dao;
    }

    /**
     * Deletes a functional requirement from a project.
     * @param projectId project of the functional requirement
     * @param frId id of functional requirement to delete
     */
    public void deleteFunctionalRequirement(UUID projectId, UUID frId) {
        repository.deleteById(frId);
        notifyClientOfProjectChange(projectId);
    }

    private void notifyClientOfProjectChange(UUID projectId) {
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED,
                null, projectId)));
    }
}

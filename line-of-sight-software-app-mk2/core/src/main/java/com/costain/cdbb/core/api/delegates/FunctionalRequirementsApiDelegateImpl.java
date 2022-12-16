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

package com.costain.cdbb.core.api.delegates;

import com.costain.cdbb.api.FunctionalRequirementsApiDelegate;
import com.costain.cdbb.model.FunctionalRequirement;
import com.costain.cdbb.model.FunctionalRequirementWithId;
import com.costain.cdbb.model.helpers.FunctionalRequirementHelper;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/functional-requirements.
 */
@Service
public class FunctionalRequirementsApiDelegateImpl implements FunctionalRequirementsApiDelegate {

    @Autowired
    private FunctionalRequirementRepository repository;

    @Autowired
    private FunctionalRequirementHelper frHelper;

    /**
     * Fetch all the functional requirements for a project.
     * @param projectId the project id of the project whose functional requirements are required
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;FunctionalRequirementWithId&gt;&gt;&gt;
     * functional requirements belonging to project</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<FunctionalRequirementWithId>>> findAllFunctionalRequirements(
        UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByProjectIdOrderByNameAsc(projectId))
                .map(dao -> frHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Add a functional requirement to a project.
     * @param projectId the project id of the project to which the functional requirement is being added
     * @param functionalRequirement the functional requirement being added
     * @return Mono&lt;ResponseEntity&lt;FunctionalRequirementWithId&gt;&gt; the functional requirement added
     */
    @Override
    public Mono<ResponseEntity<FunctionalRequirementWithId>> addFunctionalRequirement(
        UUID projectId, Mono<FunctionalRequirement> functionalRequirement, ServerWebExchange exchange) {
        return functionalRequirement.map(dto -> frHelper.fromDto(projectId, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> frHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update a functional requirement in a project.
     * @param projectId the project id of the project to which the functional requirement is being updated
     * @param functionalRequirementId the id of the functional requirement to update
     * @param functionalRequirement the updated functional requirement
     * @return Mono&lt;ResponseEntity&lt;FunctionalRequirementWithId&gt;&gt; the functional requirement updated
     */
    @Override
    public Mono<ResponseEntity<FunctionalRequirementWithId>> updateFunctionalRequirement(
        UUID projectId, UUID functionalRequirementId,
            Mono<FunctionalRequirement> functionalRequirement, ServerWebExchange exchange) {
        return functionalRequirement.map(dto -> frHelper.fromDto(projectId, functionalRequirementId, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> frHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a functional requirement from a project.
     * @param projectId the project id of the project from which the functional requirement is being deleted
     * @param functionalRequirementId the id of the functional requirement being deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteFunctionalRequirement(
        UUID projectId, UUID functionalRequirementId, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> frHelper.deleteFunctionalRequirement(projectId, functionalRequirementId))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

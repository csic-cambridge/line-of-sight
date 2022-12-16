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

import com.costain.cdbb.api.ProjectOrganisationalObjectivesApiDelegate;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveUpdate;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveWithId;
import com.costain.cdbb.model.helpers.ProjectOrganisationalObjectiveHelper;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/project-organisational-objectives.
 */

@Service
public class ProjectOrganisationalObjectivesApiDelegateImpl implements ProjectOrganisationalObjectivesApiDelegate {

    @Autowired
    private ProjectOrganisationalObjectiveRepository repository;

    @Autowired
    private ProjectOrganisationalObjectiveHelper pooHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Fetch all the project organisational objectives for a project.
     * @param projectId the project id of the project whose project organisational objectives are required
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectOrganisationalObjectiveWithId&gt;&gt;&gt;
     * project organisational objectives belonging to project</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<ProjectOrganisationalObjectiveWithId>>> findAllProjectOrganisationalObjectives(
        UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(pooHelper.findByProjectActive(projectId))
            .map(dao -> pooHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update a project organisational objective in a project.
     * @param projectId the project id of the project to which the project organisational objective is being updated
     * @param pooId the id of the project organisational objective to update
     * @param projectOrganisationalObjectiveWithId the updated project organisational objective
     * @return <p>Mono&lt;ResponseEntity&lt;projectOrganisationalObjectiveWithId&gt;&gt;
     * the project organisational objective updated</p>
     */
    @Override
    public Mono<ResponseEntity<ProjectOrganisationalObjectiveWithId>>
        updateProjectOrganisationalObjective(UUID projectId,
                                             UUID pooId,
                                             Mono<ProjectOrganisationalObjectiveUpdate>
                                                 projectOrganisationalObjectiveWithId,
                                             ServerWebExchange exchange) {
        return projectOrganisationalObjectiveWithId.map(dto ->
                transactionTemplate.execute(transactionStatus -> pooHelper.fromDto(dto)))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> pooHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project organisational objective from a project.
     * @param projectId the project id of the project from which the project organisational objective is being deleted
     * @param pooId the id of the project organisational objective being deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteProjectOrganisationalObjective(UUID projectId,
                                                                           UUID pooId,
                                                                           ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(pooId))

            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

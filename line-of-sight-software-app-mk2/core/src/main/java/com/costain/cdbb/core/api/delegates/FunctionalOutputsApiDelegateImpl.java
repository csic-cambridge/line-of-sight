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

import com.costain.cdbb.api.FunctionalOutputsApiDelegate;
import com.costain.cdbb.model.FunctionalOutput;
import com.costain.cdbb.model.FunctionalOutputWithId;
import com.costain.cdbb.model.helpers.FunctionalOutputHelper;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/functional-outputs.
 */
@Service
public class FunctionalOutputsApiDelegateImpl implements FunctionalOutputsApiDelegate {

    private FunctionalOutputRepository repository;
    private FunctionalOutputHelper foHelper;

    @Autowired
    public void setRepository(FunctionalOutputRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setFoHelper(FunctionalOutputHelper foHelper) {
        this.foHelper = foHelper;
    }

    /**
     * Fetch all the functional outputs for a project.
     * @param projectId the project id of the project whose functional outputs are required
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;FunctionalOutputWithId&gt;&gt;&gt;
     * functional outputs belonging to project</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<FunctionalOutputWithId>>> findFunctionalOutputsByProject(
        UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByProjectId(projectId))
                .map(dao -> foHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Add a functional output to a project.
     * @param projectId the project id of the project to which the functional output is being added
     * @param functionalOutput the functional output being added
     * @return Mono&lt;ResponseEntity&lt;FunctionalOutputWithId&gt;&gt; the functional output added
     */
    @Override
    public Mono<ResponseEntity<FunctionalOutputWithId>> addFunctionalOutput(
        UUID projectId, Mono<FunctionalOutput> functionalOutput, ServerWebExchange exchange) {
        return functionalOutput.map(dto -> foHelper.inputFromDto(projectId,null, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update a functional output in a project.
     * @param projectId the project id of the project to which the functional output is being updated
     * @param functionalOutputId the id of the functional output to update
     * @param functionalOutput the updated functional output
     * @return Mono&lt;ResponseEntity&lt;FunctionalOutputWithId&gt;&gt; the functional output updated
     */
    @Override
    public Mono<ResponseEntity<FunctionalOutputWithId>> updateFunctionalOutput(
        UUID projectId, UUID functionalOutputId,
        Mono<FunctionalOutput> functionalOutput, ServerWebExchange exchange) {
        return functionalOutput.map(dto -> foHelper.inputFromDto(projectId, functionalOutputId, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a functional output from a project.
     * @param projectId the project id of the project from which the functional output is being deleted
     * @param functionalOutputId the id of the functional output being deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteFunctionalOutput(UUID projectId, UUID functionalOutputId,
                                                             ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(functionalOutputId))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

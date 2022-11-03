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

import com.costain.cdbb.api.OrganisationalObjectivesApiDelegate;
import com.costain.cdbb.model.OrganisationalObjective;
import com.costain.cdbb.model.OrganisationalObjectiveWithId;
import com.costain.cdbb.model.helpers.OrganisationalObjectiveHelper;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Handles the api calls from a client with root /api/organisational-objectives.
 */

@Service
public class OrganisationalObjectivesApiDelegateImpl implements OrganisationalObjectivesApiDelegate {

    private OrganisationalObjectiveRepository repository;
    private OrganisationalObjectiveHelper ooHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    public void setRepository(OrganisationalObjectiveRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setOoHelper(OrganisationalObjectiveHelper ooHelper) {
        this.ooHelper = ooHelper;
    }

    /**
     * Fetch all organisational objectives.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;OrganisationalObjectiveWithId&gt;&gt;&gt;
     * organisational objectives</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<OrganisationalObjectiveWithId>>> findAllOrganisationalObjectives(
            ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByIsDeletedOrderByNameAsc(false))
            .map(dao -> ooHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Add an organisational objective.
     * @param organisationalObjective the organisational objective being added
     * @return Mono&lt;ResponseEntity&lt;OrganisationalObjectiveWithId&gt;&gt; the organisational objective added
     */
    @Override
    public Mono<ResponseEntity<OrganisationalObjectiveWithId>> addOrganisationalObjective(
            Mono<OrganisationalObjective> organisationalObjective, ServerWebExchange exchange) {
        return organisationalObjective.map(dto -> ooHelper.fromDto(dto))
            .flatMap(dao -> Mono.fromCallable(() ->
                    transactionTemplate.execute(transactionStatus -> ooHelper.createOo(dao)))
            )
            .map(savedDao -> ooHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update an organisational objective.
     * @param organisationalObjectiveId the id of the organisational objective to update
     * @param organisationalObjective the updated functional requirement
     * @return Mono&lt;ResponseEntity&lt;OrganisationalObjectiveWithId&gt;&gt; the organisational objective updated
     */
    @Override
    public Mono<ResponseEntity<OrganisationalObjectiveWithId>>
        updateOrganisationalObjective(UUID organisationalObjectiveId,
            Mono<OrganisationalObjective> organisationalObjective, ServerWebExchange exchange) {
        return organisationalObjective.map(dto -> ooHelper.fromDto(organisationalObjectiveId, dto))
        .flatMap(dao ->
            Mono.fromCallable(() -> transactionTemplate.execute(transactionStatus -> ooHelper.updateOo(dao)))
        )
        .map(savedDao -> ooHelper.fromDao(savedDao))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete an organisational objective.
     * @param organisationalObjectiveId the id of the organisational objective being deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>>
        deleteOrganisationalObjective(UUID organisationalObjectiveId, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> ooHelper.markAsDeleted(organisationalObjectiveId))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

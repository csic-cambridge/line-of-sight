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


@Service
public class ProjectOrganisationalObjectivesApiDelegateImpl implements ProjectOrganisationalObjectivesApiDelegate {

    private ProjectOrganisationalObjectiveRepository repository;
    private ProjectOrganisationalObjectiveHelper pooHelper;

    @Autowired
    public void setRepository(ProjectOrganisationalObjectiveRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setPooHelper(ProjectOrganisationalObjectiveHelper pooHelper) {
        this.pooHelper = pooHelper;
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Mono<ResponseEntity<Flux<ProjectOrganisationalObjectiveWithId>>> findAllProjectOrganisationalObjectives(
        UUID id, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(pooHelper.findByProjectActive(id))
            .map(dao -> pooHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<ProjectOrganisationalObjectiveWithId>>
        updateProjectOrganisationalObjective(UUID projectid,
                                             UUID pooid,
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

    @Override
    public Mono<ResponseEntity<Void>> deleteProjectOrganisationalObjective(UUID projectid,
                                                                           UUID pooid,
                                                                           ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(pooid))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

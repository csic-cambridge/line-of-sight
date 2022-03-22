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

import com.costain.cdbb.api.FunctionalObjectivesApiDelegate;
import com.costain.cdbb.model.FunctionalObjective;
import com.costain.cdbb.model.FunctionalObjectiveWithId;
import com.costain.cdbb.model.helpers.FunctionalObjectiveHelper;
import com.costain.cdbb.repositories.FunctionalObjectiveRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FunctionalObjectivesApiDelegateImpl implements FunctionalObjectivesApiDelegate {

    private FunctionalObjectiveRepository repository;
    private FunctionalObjectiveHelper foHelper;

    @Autowired
    public void setRepository(FunctionalObjectiveRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setFoHelper(FunctionalObjectiveHelper foHelper) {
        this.foHelper = foHelper;
    }

    @Override
    public Mono<ResponseEntity<Flux<FunctionalObjectiveWithId>>> findAllFunctionalObjectives(
            ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findAll())
                .map(dao -> foHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalObjectiveWithId>> findFunctionalObjectiveById(UUID id,
            ServerWebExchange exchange) {
        return Mono.fromCallable(() -> repository.findById(id).orElse(null))
            .map(dao -> foHelper.fromDao(dao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalObjectiveWithId>> addFunctionalObjective(
            Mono<FunctionalObjective> functionalObjective, ServerWebExchange exchange) {
        return functionalObjective.map(dto -> foHelper.fromDto(dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalObjectiveWithId>> upsertFunctionalObjective(UUID id,
            Mono<FunctionalObjective> functionalObjective, ServerWebExchange exchange) {
        return functionalObjective.map(dto -> foHelper.fromDto(id, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteFunctionalObjective(UUID id, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(id))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

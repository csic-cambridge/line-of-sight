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

    @Override
    public Mono<ResponseEntity<Flux<FunctionalOutputWithId>>> findFunctionalOutputsByProject(
        UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByProjectId(projectId))
                .map(dao -> foHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalOutputWithId>> findFunctionalOutputById(UUID projectId, UUID id,
            ServerWebExchange exchange) {
        return FunctionalOutputsApiDelegate.super.findFunctionalOutputById(projectId, id, exchange);
    }

    @Override
    public Mono<ResponseEntity<FunctionalOutputWithId>> addFunctionalOutput(
        UUID projectId, Mono<FunctionalOutput> functionalOutput, ServerWebExchange exchange) {
        return functionalOutput.map(dto -> foHelper.inputFromDto(projectId,null, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalOutputWithId>> updateFunctionalOutput(
        UUID projectId, UUID id,
        Mono<FunctionalOutput> functionalOutput, ServerWebExchange exchange) {
        return functionalOutput.map(dto -> foHelper.inputFromDto(projectId, id, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> foHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteFunctionalOutput(UUID projectId, UUID id, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(id))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

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

@Service
public class FunctionalRequirementsApiDelegateImpl implements FunctionalRequirementsApiDelegate {

    private FunctionalRequirementRepository repository;
    private FunctionalRequirementHelper frHelper;

    @Autowired
    public void setRepository(FunctionalRequirementRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setFrHelper(FunctionalRequirementHelper frHelper) {
        this.frHelper = frHelper;
    }

    @Override
    public Mono<ResponseEntity<Flux<FunctionalRequirementWithId>>> findAllFunctionalRequirements(
        UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findByProjectIdOrderByNameAsc(projectId))
                .map(dao -> frHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalRequirementWithId>> findFunctionalRequirementById(
        UUID projectId, UUID id, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> repository.findById(id).orElse(null))
            .map(dao -> frHelper.fromDao(dao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalRequirementWithId>> addFunctionalRequirement(
        UUID projectId, Mono<FunctionalRequirement> functionalRequirement, ServerWebExchange exchange) {
        System.out.println("addFunctionalRequirement: projectId=" + projectId + ", functionalRequirement "
            + functionalRequirement.toString());
        return functionalRequirement.map(dto -> frHelper.fromDto(projectId, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> frHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalRequirementWithId>> updateFunctionalRequirement(
        UUID projectId, UUID id,
            Mono<FunctionalRequirement> functionalRequirement, ServerWebExchange exchange) {
        return functionalRequirement.map(dto -> frHelper.fromDto(projectId, id, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> frHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteFunctionalRequirement(
        UUID projectId, UUID id, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(id))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

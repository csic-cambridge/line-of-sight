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


import com.costain.cdbb.api.FirsApiDelegate;
import com.costain.cdbb.model.Firs;
import com.costain.cdbb.model.FunctionalOutputWithId;
import com.costain.cdbb.model.helpers.FunctionalOutputHelper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 *Handles the api calls from a client with root /api/firs.
 */
@Service
public class FirsApiDelegateImpl implements FirsApiDelegate {

    @Autowired
    FunctionalOutputHelper foHelper;

    /**
     * Fetches all firs in the system in alphabetical order.
     * @return Mono&lt;ResponseEntity&lt;String&gt;&gt; all firs
     */
    @Override
    public Mono<ResponseEntity<Flux<Firs>>> fetchFirs(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(foHelper.findAllFirs())
            .map(dao -> new Firs().id(dao.getId().toString()).firs(dao.getFirs())))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Imports functional outputs and firs.
     * @return Mono&lt;ResponseEntity&lt;Flux&lt;FunctionalOutputWithId&gt;&gt;&gt; imported functional outputs
     */
    @Override
    public Mono<ResponseEntity<Flux<FunctionalOutputWithId>>>
        importFunctionalInformationRequirements(UUID projectid,
                                                Mono<String> body,
                                                ServerWebExchange exchange) {
        return body.map(dto -> Flux.fromIterable(foHelper.importFirs(dto, projectid))
            .map(savedDao -> foHelper.fromDao(savedDao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

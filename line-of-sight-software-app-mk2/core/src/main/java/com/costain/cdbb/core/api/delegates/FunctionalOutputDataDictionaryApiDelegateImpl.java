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


import com.costain.cdbb.api.FunctionalOutputDataDictionaryApiDelegate;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.FunctionalOutputDataDictionary;
import com.costain.cdbb.model.helpers.FunctionalOutputDataDictionaryHelper;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/functional-output-data-dictionary.
 */
@Service
public class FunctionalOutputDataDictionaryApiDelegateImpl implements FunctionalOutputDataDictionaryApiDelegate {

    @Autowired
    private FunctionalOutputDataDictionaryRepository ddRepository;

    @Autowired
    private FunctionalOutputDataDictionaryHelper dataDictionaryHelper;

    @Autowired
    private FunctionalOutputDataDictionaryEntryRepository ddeRepository;

    /**
     * Fetch all the functional output data dictionaries.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;FunctionalOutputDataDictionary&gt;&gt;&gt;
     * functional output data dictionaries</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<FunctionalOutputDataDictionary>>>
        findAllFunctionalOutputDataDictionaries(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(ddRepository.findAll())
                .map(dao -> dataDictionaryHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Fetch all the entries for a requested functional output data dictionary.
     * @param dataDictionaryId the id of the required functional output data dictionary
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;DataDictionaryEntry&gt;&gt;&gt;
     * functional output data dictionary entries</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<DataDictionaryEntry>>> findAllFunctionalOutputDataDictionaryEntries(

        UUID dataDictionaryId, ServerWebExchange exchange) {
        return Mono.fromCallable(() ->
                Flux.fromIterable(ddeRepository.findByFoDictionaryIdOrderByEntryId(dataDictionaryId))
                .map(dao -> new DataDictionaryEntry()
                    .entryId(dao.getEntryId())
                    .text(dao.getEntryId() + "-" + dao.getText())))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<FunctionalOutputDataDictionary>> importFunctionalOutputDataDictionary(Mono<String> body,
                                                                               ServerWebExchange exchange) {
        return body.map(dto -> dataDictionaryHelper.importDictionary(dto))
            .map(savedDao -> dataDictionaryHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

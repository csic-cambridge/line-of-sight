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


@Service
public class FunctionalOutputDataDictionaryApiDelegateImpl implements FunctionalOutputDataDictionaryApiDelegate {

    private FunctionalOutputDataDictionaryRepository ddRepository;
    private FunctionalOutputDataDictionaryEntryRepository ddeRepository;

    private FunctionalOutputDataDictionaryHelper dataDictionaryHelper;

    @Autowired
    public void setProjectHelper(FunctionalOutputDataDictionaryHelper dataDictionaryHelper) {
        this.dataDictionaryHelper = dataDictionaryHelper;
    }

    @Autowired
    public void setDdRepository(FunctionalOutputDataDictionaryRepository ddRepository) {
        this.ddRepository = ddRepository;
    }

    @Autowired
    public void setDdeRepository(FunctionalOutputDataDictionaryEntryRepository ddeRepository) {
        this.ddeRepository = ddeRepository;
    }

    @Override
    public Mono<ResponseEntity<Flux<FunctionalOutputDataDictionary>>>
        findAllFunctionalOutputDataDictionaries(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(ddRepository.findAll())
                .map(dao -> dataDictionaryHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<DataDictionaryEntry>>> findAllFunctionalOutputDataDictionaryEntries(
        UUID id, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(ddeRepository.findByFoDictionaryId(id))
                .map(dao -> new DataDictionaryEntry()
                    .id(dao.getId())
                    .text(dao.getId() + "-" + dao.getText())))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

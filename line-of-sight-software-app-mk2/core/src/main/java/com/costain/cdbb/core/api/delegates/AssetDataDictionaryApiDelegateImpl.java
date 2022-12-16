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

import com.costain.cdbb.api.AssetDataDictionaryApiDelegate;
import com.costain.cdbb.model.AssetDataDictionary;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.helpers.AssetDataDictionaryHelper;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *Handles the api calls from a client with root /api/asset-data-dictionary.
 */
@Service
public class AssetDataDictionaryApiDelegateImpl implements AssetDataDictionaryApiDelegate {

    @Autowired
    private AssetDataDictionaryEntryRepository ddeRepository;

    @Autowired
    private AssetDataDictionaryRepository ddRepository;

    @Autowired
    private AssetDataDictionaryHelper assetDataDictionaryHelper;


    /**
     * Fetch all the asset data dictionaries.
     * @return Mono&lt;ResponseEntity&lt;Flux&lt;AssetDataDictionary&gt;&gt;&gt; asset data dictionaries
     */

    @Override
    public Mono<ResponseEntity<Flux<AssetDataDictionary>>> findAllAssetDataDictionaries(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(ddRepository.findAll())
                .map(dao -> assetDataDictionaryHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Fetch all the entries for a requested asset data dictionary.
     * @param dataDictionaryId the id of the required asset data dictionary
     * @return Mono&lt;ResponseEntity&lt;Flux&lt;DataDictionaryEntry&gt;&gt;&gt; asset data dictionary entries
     */
    @Override
    public Mono<ResponseEntity<Flux<DataDictionaryEntry>>> findAllAssetDataDictionaryEntries(
        UUID dataDictionaryId,ServerWebExchange exchange) {
        return Mono.fromCallable(() ->
                Flux.fromIterable(ddeRepository.findByAssetDictionaryIdOrderByEntryId(dataDictionaryId))
                .map(dao -> new DataDictionaryEntry()
                    .entryId(dao.getEntryId())
                    .text(dao.getEntryId() + "-" + dao.getText())))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Import a new asset data dictionary.
     * @param body first line is name of dictionary followed by comma separated key value entries
     * @return <p>Mono&lt;ResponseEntity&lt;AssetDataDictionary&gt;&gt;
     * asset data dictionary</p>
     */
    @Override
    public Mono<ResponseEntity<AssetDataDictionary>> importAssetDataDictionary(Mono<String> body,
                                                                               ServerWebExchange exchange) {
        return body.map(dto -> assetDataDictionaryHelper.importDictionary(dto))
            .map(savedDao -> assetDataDictionaryHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

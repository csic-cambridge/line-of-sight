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

import com.costain.cdbb.api.AssetsApiDelegate;
import com.costain.cdbb.model.Asset;
import com.costain.cdbb.model.AssetWithId;
import com.costain.cdbb.model.helpers.AssetHelper;
import com.costain.cdbb.repositories.AssetRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/assets api.
 */

@Service
public class AssetsApiDelegateImpl implements AssetsApiDelegate {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetHelper assetHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * Fetch all the assets for a project.
     * @param projectId the project id of the project whose assets are required
     * @return Mono&lt;ResponseEntity&lt;Flux&lt;AssetWithId&gt;&gt;&gt; assets belonging to project
     */
    @Override
    public Mono<ResponseEntity<Flux<AssetWithId>>> findAssetsByProject(UUID projectId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(
                assetRepository.findByProjectIdOrderByDataDictionaryEntry_EntryId(projectId))
                .map(dao -> assetHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Add an asset to a project.
     * @param projectId the project id of the project to which the asset is being added
     * @param asset the asset being added
     * @return Mono&lt;ResponseEntity&lt;AssetWithId&gt;&gt; the asset added
     */
    @Override
    public Mono<ResponseEntity<AssetWithId>> addAsset(UUID projectId, Mono<Asset> asset, ServerWebExchange exchange) {
        return asset.map(dto -> assetHelper.fromDto(null, dto, projectId))
            .flatMap(dao -> Mono.fromCallable(() ->
                transactionTemplate.execute(transactionStatus -> assetRepository.save(dao))))
            .map(savedDao -> assetHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update an asset in a project.
     * @param projectId the project id of the project to which the asset is being updated
     * @param assetId id of the asset to update
     * @param asset the updated asset
     * @return Mono&lt;ResponseEntity&lt;AssetWithId&gt;&gt; the asset updated
     */
    @Override
    public Mono<ResponseEntity<AssetWithId>> updateAsset(UUID projectId, UUID assetId,
                                                         Mono<Asset> asset, ServerWebExchange exchange) {
        return asset.map(dto -> assetHelper.fromDto(assetId, dto, projectId))
            .flatMap(dao -> Mono.fromCallable(() -> assetRepository.save(dao)))
            .map(savedDao -> assetHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete an asset from a project.
     * @param projectId the project id of the project from which the asset is being deleted
     * @param assetId the id of the asset being deleted
     * @return Mono&lt;ResponseEntity&lt;Void&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteAsset(UUID projectId, UUID assetId, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> assetHelper.deleteAsset(projectId, assetId))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

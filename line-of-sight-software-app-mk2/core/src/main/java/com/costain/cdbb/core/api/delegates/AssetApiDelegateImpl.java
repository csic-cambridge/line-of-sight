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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssetApiDelegateImpl implements AssetsApiDelegate {

    private AssetRepository repository;
    private AssetHelper assetHelper;

    @Autowired
    public void setRepository(AssetRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAssetHelper(AssetHelper assetHelper) {
        this.assetHelper = assetHelper;
    }

    @Override
    public Mono<ResponseEntity<Flux<AssetWithId>>> findAllAssets(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findAll())
                .map(dao -> assetHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<AssetWithId>> findAssetById(UUID id, ServerWebExchange exchange) {
        return AssetsApiDelegate.super.findAssetById(id, exchange);
    }

    @Override
    public Mono<ResponseEntity<AssetWithId>> addAsset(Mono<Asset> asset, ServerWebExchange exchange) {
        return asset.map(dto -> assetHelper.fromDto(dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> assetHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<AssetWithId>> upsertAsset(UUID id, Mono<Asset> asset, ServerWebExchange exchange) {
        return asset.map(dto -> assetHelper.fromDto(id, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> assetHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAsset(UUID id, ServerWebExchange exchange) {
        ResponseEntity<Void> re = ResponseEntity.noContent().build();
        return Mono.fromRunnable(() -> repository.deleteById(id))
            .map(x -> re)
            .defaultIfEmpty(re);
    }
}

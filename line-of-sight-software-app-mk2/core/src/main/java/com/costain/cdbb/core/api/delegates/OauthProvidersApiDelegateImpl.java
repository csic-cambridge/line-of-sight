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


import com.costain.cdbb.api.OauthProvidersApiDelegate;
import com.costain.cdbb.core.config.Oauth2Provider;
import com.costain.cdbb.model.LoginProvider;
import com.costain.cdbb.model.helpers.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/oauth-providers.
 */

@Service
public class OauthProvidersApiDelegateImpl implements OauthProvidersApiDelegate {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private Oauth2Provider oauth2Provider;

    /**
     * Fetch the oauth providers list defined in configuration file.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;LoginProvider&gt;&gt;&gt;</p>
    */
    @Override
    public Mono<ResponseEntity<Flux<LoginProvider>>> fetchOauth2ProviderDetails(Mono<Object> body,
                                                                                ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(oauth2Provider.getProviders())
                .map(dao -> loginHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

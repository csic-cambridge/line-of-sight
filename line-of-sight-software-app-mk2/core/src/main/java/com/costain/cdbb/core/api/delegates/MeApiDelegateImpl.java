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

import com.costain.cdbb.api.MeApiDelegate;
import com.costain.cdbb.model.User;
import com.costain.cdbb.model.helpers.UserHelper;
import com.costain.cdbb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/me.
 */

@Service
public class MeApiDelegateImpl implements MeApiDelegate {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserHelper userHelper;

    /**
     * Fetch the logged in user.
     * @return <p>Mono&lt;ResponseEntity&lt;User&gt;&gt;</p>
     */
    @Override
    public Mono<ResponseEntity<User>> fetchLoggedInUser(ServerWebExchange exchange) {
        return exchange.getPrincipal().map(principal ->
                userHelper.fromDao(repository.findByEmailAddress(userHelper.getEmailAddress(principal)))
            )
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

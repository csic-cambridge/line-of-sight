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

import com.costain.cdbb.api.UserApiDelegate;
import com.costain.cdbb.model.User;
import com.costain.cdbb.model.helpers.UserHelper;
import com.costain.cdbb.repositories.UserRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handles the api calls from a client with root /api/user.
 */

@Service
public class UserApiDelegateImpl implements UserApiDelegate {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserHelper userHelper;

    /**
     * Fetch all users.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;User&gt;&gt;&gt; users</p>
     */
    @Override
    public Mono<ResponseEntity<Flux<User>>> findAllUsers(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> Flux.fromIterable(repository.findAllByOrderByEmailAddressAsc())
                .map(dao -> userHelper.fromDao(dao)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Fetch user by id.
     * @param userId id of user to be fetched
     * @return <p>Mono&lt;ResponseEntity&lt;User&gt;&gt; user</p>
     */
    @Override
    public Mono<ResponseEntity<User>> findUserById(UUID userId, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> userHelper.fromDao(repository.findById(userId).orElse(null)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update user.
     * @param userId user id
     * @return <p>Mono&lt;ResponseEntity&lt;User&gt;&gt; user</p>
     */
    @Override
    public Mono<ResponseEntity<User>> updateUser(UUID userId,
            Mono<User> user,
            ServerWebExchange exchange) {
        // only used to set super user status
        return user.map(dto -> userHelper.updateFromDto(userId, dto))
            .flatMap(dao -> Mono.fromCallable(() -> repository.save(dao)))
            .map(savedDao -> userHelper.fromDao(savedDao))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

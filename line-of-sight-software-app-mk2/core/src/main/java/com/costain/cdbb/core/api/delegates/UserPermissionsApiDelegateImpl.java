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


import com.costain.cdbb.api.UserPermissionsApiDelegate;
import com.costain.cdbb.model.UserPermissions;
import com.costain.cdbb.model.helpers.UserPermissionsHelper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Handles the api calls from a client with root /api/user-permissions.
 */

@Service
public class UserPermissionsApiDelegateImpl implements UserPermissionsApiDelegate {

    @Autowired
    private UserPermissionsHelper permissionsHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Fetch user permissions for a given user.
     * @param userId id of user
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;UserPermissions&gt;&gt;&gt;
     * user permissions for user</p>
     */
    @Override
    public Mono<ResponseEntity<UserPermissions>> findPermissionsByUser(String userId, ServerWebExchange exchange) {
        // no principal for integration test so try both access methods
        return exchange.getPrincipal().map(principal ->
                 permissionsHelper.fromDao(principal, userId))
            .defaultIfEmpty(permissionsHelper.fromDao(null, userId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update user permissions for a given user.
     * @param userId id of user
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;UserPermissions&gt;&gt;&gt;
     * updated user permissions for user</p>
     */
    @Override
    public Mono<ResponseEntity<UserPermissions>> updatePermissionsForUser(UUID userId,
                                                                          Mono<UserPermissions> permissions,
                                                                          ServerWebExchange exchange) {
        return permissions.map(dto -> permissionsHelper.fromDto(dto, userId))
            .flatMap(daos -> Mono.fromCallable(() -> transactionTemplate.execute(transactionStatus ->
               permissionsHelper.savePermissions(daos, userId)))
            .map(x -> permissionsHelper.toDto(userId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build()));
    }
}

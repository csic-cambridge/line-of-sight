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

import com.costain.cdbb.api.ProjectPermissionsApiDelegate;
import com.costain.cdbb.model.ProjectPermissions;
import com.costain.cdbb.model.helpers.ProjectPermissionsHelper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Handles the api calls from a client with root /api/project-permissions.
 */

@Service
public class ProjectPermissionsApiDelegateImpl implements ProjectPermissionsApiDelegate {

    @Autowired
    private ProjectPermissionsHelper permissionsHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Fetch project permissions for a given project and user.
     * @param userId id of user
     * @param projectId id of project
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectPermissions&gt;&gt;&gt;
     * project permissions for project/user</p>
     */
    @Override
    public Mono<ResponseEntity<ProjectPermissions>> findPermissionsByUserAndProject(String userId,
                                                                                    UUID projectId,
                                                                                    ServerWebExchange exchange) {
        return exchange.getPrincipal().map(principal ->
                permissionsHelper.fromDao(
                    principal,
                    userId,
                    projectId
                    ))
            .defaultIfEmpty(permissionsHelper.fromDao(null, userId, projectId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Update project permissions for a given project and user.
     * @param userId id of user
     * @param projectId id of project
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;ProjectPermissions&gt;&gt;&gt;
     * updated project permissions for project/user</p>
     */
    @Override
    public Mono<ResponseEntity<ProjectPermissions>> updatePermissionsForUserAndProject(UUID userId,
                                                                                UUID projectId,
                                                                                Mono<ProjectPermissions> permissions,
                                                                                ServerWebExchange exchange) {
        return permissions.map(dto -> permissionsHelper.fromDto(dto, userId, projectId))
            .flatMap(daos -> Mono.fromCallable(() -> transactionTemplate.execute(transactionStatus ->
               permissionsHelper.savePermissions(daos, userId, projectId)))
            .map(x -> permissionsHelper.toDto(userId, projectId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build()));
    }
}


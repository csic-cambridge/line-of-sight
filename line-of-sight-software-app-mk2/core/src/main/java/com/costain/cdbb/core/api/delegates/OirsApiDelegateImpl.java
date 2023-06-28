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

import com.costain.cdbb.api.OirsApiDelegate;
import com.costain.cdbb.model.helpers.OirAirLinkHelper;
import java.util.UUID;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



/**
 * Handles the api calls from a client with root /api/oirs.
 */

@Service
public class OirsApiDelegateImpl implements OirsApiDelegate {
    @Autowired
    OirAirLinkHelper linkHelper;

    /**
     * Fetch all organisational objectives.
     * @return <p>Mono&lt;ResponseEntity&lt;Flux&lt;OrganisationalObjectiveWithId&gt;&gt;&gt;
     * organisational objectives</p>
     */
    @Override
    public Mono<ResponseEntity<Void>> linkOirsAirs(UUID projectid,
                                                   Integer linkunlink,
                                                   Mono<String> body,
                                                   ServerWebExchange exchange) {
        return body.map(dto -> {
            try {
                linkHelper.linkOirToAir(linkunlink == 1, projectid, dto);
                return ResponseEntity.ok().build();
            } catch (JSONException e) {
                return ResponseEntity.badRequest().build();
            }
        });
    }
}

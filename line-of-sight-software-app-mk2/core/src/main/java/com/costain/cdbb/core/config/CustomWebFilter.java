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

package com.costain.cdbb.core.config;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;



@Component
public class CustomWebFilter implements WebFilter {
    private static final String INDEX_PAGE = "/";
    public static final String LOGIN_ROUTE = "/login";
    public static final String DASHBOARD_ROUTE = "/dashboard";
    public static final String OOGRAPH_ROUTE = "/oograph";
    public static final String PROJECT_ROUTE = "/project";
    public static final String SUPERUSER_ROUTE = "/superuser";

    private static final Set<String> routes = new HashSet<>(
        Arrays.asList(LOGIN_ROUTE, DASHBOARD_ROUTE, OOGRAPH_ROUTE, PROJECT_ROUTE, SUPERUSER_ROUTE));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (routes.contains(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange.mutate().request(
                exchange.getRequest().mutate().path(INDEX_PAGE).build()).build());
        }
        return chain.filter(exchange);
    }
}

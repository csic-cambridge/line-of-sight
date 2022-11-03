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

import com.costain.cdbb.model.helpers.AuthoritiesHelper;
import com.costain.cdbb.model.helpers.UserHelper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.NDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;




@Component
public class AuthManagerHandler implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    AuthoritiesHelper authoritiesHelper;

    @Autowired
    UserHelper userHelper;

    /**
     * Determines if access is granted for a specific authentication and object.
     * @param authentication authentication object (Mono)
     * @param authContext the authorization context
     * @return Mono&lt;AuthorizationDecision&gt; true or false.
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authContext) {
        ServerHttpRequest request = authContext.getExchange().getRequest();
        String requestUrl = request.getPath().pathWithinApplication().value();
        System.out.println("Request=" + requestUrl);
        List<String> authoritiesRequiredForAccess =
            authoritiesHelper.getAuthoritiesRequiredForUrlAndMethod(requestUrl, request.getMethod());
        return authentication
            .filter(Authentication::isAuthenticated)
            .flatMapIterable(a -> this.getAuthoritiesForPrincipal(a.getPrincipal(), requestUrl))
            .any(c -> authoritiesRequiredForAccess.contains(String.valueOf(c)))
            .map(AuthorizationDecision::new)
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    private List<String> getAuthoritiesForPrincipal(Object principal, String url) {
        if (principal instanceof DefaultOidcUser) {
            String emailAddress =  ((DefaultOidcUser) principal).getAttribute("email");
            if (emailAddress != null) {
                this.saveUserIdForAuditting(emailAddress);
                return authoritiesHelper.getAuthoritiesForUserWithEmailAddress(emailAddress, url);
            }
        }
        return Collections.emptyList();
    }

    private void saveUserIdForAuditting(String emailAddress) {
        UUID userId = userHelper.getUserIdForEmailAddress(emailAddress);
        NDC.clear();
        NDC.push(userId == null ? "" : userId.toString());
    }
}

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


import com.costain.cdbb.model.helpers.UserPermissionsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;


@Profile("!no_security")
@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)

@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    UserPermissionsHelper userPermissionsHelper;

    @Autowired
    AuthManagerHandler authManagerHandler;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(
            new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler()
        );
        return
            http
                .authorizeExchange(authorizeExchange -> authorizeExchange
                .pathMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                // Allow CORS options requests
                .pathMatchers("/*.map").permitAll()
                .pathMatchers("/*.css").permitAll()
                .pathMatchers("/*.js").permitAll()
                .pathMatchers("/favicon.*").permitAll()
                //.pathMatchers("/login").permitAll()
                .pathMatchers("/logout").permitAll()
                .pathMatchers("/api/oauth-providers").permitAll()
                .pathMatchers("/oauth2/authorization/**").permitAll()
                .pathMatchers(CustomWebFilter.DASHBOARD_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.OOGRAPH_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.PROJECT_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.SUPERUSER_ROUTE).authenticated()
                    .pathMatchers("/api/project/import").permitAll() // TEMPORARY FOR ROB
                    .pathMatchers("/api/project/export/**").permitAll() // TEMPORARY FOR ROB
                    .pathMatchers("/api/firs/import/**").permitAll() // TEMPORARY FOR ROB
                    .pathMatchers("/api/airs/import/**").permitAll() // TEMPORARY FOR ROB
                    .pathMatchers("/api/asset-data-dictionary").permitAll() // TEMPORARY FOR ROB
                    .pathMatchers("/api/functional-output-data-dictionary").permitAll() // TEMPORARY FOR ROB

                .pathMatchers("/api/**").access(authManagerHandler)
                .pathMatchers("/").permitAll()
                .pathMatchers("/actuator/**").access(authManagerHandler)
            )
             .logout((logout) -> logout.logoutHandler(logoutHandler))
             .oauth2Login()
             .and()

            /*.formLogin(spec -> {
               spec.authenticationSuccessHandler((webFilterExchange, authentication) -> {
                   webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                   return Mono.empty();
               });
               spec.authenticationFailureHandler(
                    new ServerAuthenticationEntryPointFailureHandler(
                        new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
           })*/
            .exceptionHandling(spec -> {
                spec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
            })
            /*.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint((exchange, denied) -> {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.mutate().response(response);
                return Mono.empty();
            }))*/
            .logout(logoutSpec ->
                logoutSpec.logoutSuccessHandler((webFilterExchange, authentication) -> {
                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                })
            )

            // Disable spring security cors as we are using a web filter to configure CORS
            .cors(Customizer.withDefaults())
            .csrf(spec -> spec.disable()) //.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
            .build();
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.addCookieInitializer(builder -> builder.httpOnly(false));
        return resolver;
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            var emailAddress = new Object() {
                String value = "";
            };

            authorities.forEach(authority -> {
                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority)authority;

                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
                    // Map the claims found in idToken and/or userInfo
                    // to one or more GrantedAuthority's and add it to mappedAuthorities
                    emailAddress.value = idToken.getClaim("email");

                } else if (OAuth2UserAuthority.class.isInstance(authority)) {
                    emailAddress.value = "";
                }
            });
            // use email address to get user roles and authorities
            return userPermissionsHelper.getAuthoritiesForUserWithEmailAddress(emailAddress.value);
        };
    }
}


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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
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

public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(authorizeExchange -> authorizeExchange
                // Allow CORS options requests
                .pathMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .pathMatchers("/").permitAll()
                .pathMatchers("/*.css").permitAll()
                .pathMatchers("/*.js").permitAll()
                .pathMatchers("/favicon.*").permitAll()
                .pathMatchers("/login").permitAll()
                .pathMatchers("/logout").permitAll()
                .pathMatchers(CustomWebFilter.DASHBOARD_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.OOGRAPH_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.PROJECT_ROUTE).authenticated()
                .pathMatchers(CustomWebFilter.SUPERUSER_ROUTE).authenticated()
                .pathMatchers("/api/**").authenticated()
            )

           .formLogin(spec -> {
               spec.authenticationSuccessHandler((webFilterExchange, authentication) -> {
                   webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                   return Mono.empty();
               });
               spec.authenticationFailureHandler(
                    new ServerAuthenticationEntryPointFailureHandler(
                        new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
           })
            .exceptionHandling(spec -> {
                spec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
            })
            .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint((exchange, denied) -> {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.mutate().response(response);
                return Mono.empty();
            }))
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
}


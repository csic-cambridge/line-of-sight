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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2 helper functions.
 */
@Configuration
@ConfigurationProperties("spring.security.oauth2.client")
public class Oauth2Provider {
    private Map<String, Object> registration;

    public Map<String, Object> getRegistration() {
        return new HashMap<>(registration);
    }

    public void setRegistration(Map<String, Object> registration) {
        this.registration = new HashMap<>(registration);
    }

    /**
     * Fetch a list of the OAuth providers as defined in the configuration file.
     * @return List&lt;String&gt; providers with first character upper-cased.
     */
    public List<String> getProviders() {
        List<String> providers = new ArrayList<>(registration.size());
        for (String reg : registration.keySet()) {
            providers.add(reg.substring(0, 1).toUpperCase(Locale.getDefault())
                + reg.substring(1));
        }
        return providers;
    }
}

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

package com.costain.cdbb.model.helpers;

import com.costain.cdbb.model.LoginProvider;
import java.util.Locale;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.stereotype.Component;

/**
 * Login provider helper functions.
 */

@Component
public class LoginHelper {

    /**
     * Get the login provider for the string provider.
     * @param provider the name of the provider for which a login url is required
     * @return LoginProvider login provider object for the provider
     */
    public LoginProvider fromDao(String provider) {
        return new LoginProvider()
            .name(provider)
            .url(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
                   + "/" + provider.toLowerCase(Locale.getDefault()));
    }
}

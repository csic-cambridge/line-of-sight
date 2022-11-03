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

package com.costain.cdbb.core.helpers;


import com.costain.cdbb.core.permissions.UserPermissionTypes;
import com.costain.cdbb.model.UserDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpMethod;




public class AuthorityTestData {
    private UserDAO userDao;
    private UUID projectId;
    private HttpMethod method;
    private String url;
    private List<String> expectedAuthorities;

    public AuthorityTestData(UserDAO userDao,
                             UUID projectId,
                             HttpMethod method,
                             String url,
                             String expectedAuthority) {
        this.userDao = userDao;
        this.projectId = projectId;
        this.method = method;
        this.url = url.replace("{userid}", userDao.getUserId().toString());
        if (projectId != null) {
            this.url = this.url.replace("{projectid}", projectId.toString());
        }
        expectedAuthorities = new ArrayList<>();
        if (expectedAuthority != null) {
            expectedAuthorities.add(expectedAuthority);
        }
        expectedAuthorities.add(UserPermissionTypes.ROLE_ADMIN);
        this.expectedAuthorities = expectedAuthorities;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getExpectedAuthorities() {
        return expectedAuthorities;
    }
}

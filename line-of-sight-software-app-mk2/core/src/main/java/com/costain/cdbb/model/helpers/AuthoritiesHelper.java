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


import com.costain.cdbb.core.permissions.ApiPermissions;
import com.costain.cdbb.core.permissions.ProjectPermissionId;
import com.costain.cdbb.core.permissions.ProjectPermissionTypes;
import com.costain.cdbb.core.permissions.UserPermissionId;
import com.costain.cdbb.core.permissions.UserPermissionTypes;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.repositories.UserPermissionRepository;
import com.costain.cdbb.repositories.UserProjectPermissionRepository;
import com.costain.cdbb.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;


/**
 * Provides helper functions for fetching permissions required for a url.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthoritiesHelper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Autowired
    UserProjectPermissionRepository userProjectPermissionRepository;

    /**
     * Fetch the authorities for a user for a url.
     * @param emailAddress the email address of the user
     * @param url the url for which the authorities are required
     * @return List&lt;String&gt; list of authorities
     */
    public List<String> getAuthoritiesForUserWithEmailAddress(String emailAddress, String url) {
        // Note: the permissions could be cached to save a db lookup each time
        // This would only work though for single server implementations
        List<String> authorities = new ArrayList<>();
        UUID projectId = this.getProjectIdFromUrl(url);

        UserDAO userDao = userRepository.findByEmailAddress(emailAddress);
        if (userDao.isSuperUser()) {
            authorities.add(UserPermissionTypes.ROLE_ADMIN);
        } else {
            authorities.add(UserPermissionTypes.ROLE_USER);
            userPermissionRepository.findById_UserId(userDao.getUserId()).forEach(
                userPermissionDao -> {
                    authorities.add(UserPermissionTypes.getAuthorityNameForId(
                        new UserPermissionId(userPermissionDao.getId().getPermissionId())));
                }
            );
            userProjectPermissionRepository
                .findById_UserIdAndId_ProjectId(userDao.getUserId(), projectId).forEach(
                    userProjectPermissionDao -> {
                        authorities.add(ProjectPermissionTypes.getAuthorityNameForId(
                            new ProjectPermissionId(userProjectPermissionDao.getId().getPermissionId())));
                    });
        }
        return authorities;
    }

    /**
     *  Fetches a list of authorities required by the system to access the api url with the given method
     *  Any one of the authorities can be matched to gain access.
     * @param url the api url for which access is required
     * @param method the http method being used with the api call
     * @return List&lt;String&gt; list of authorities
     */
    public List<String> getAuthoritiesRequiredForUrlAndMethod(String url, HttpMethod method) {
        List<String> authoritiesRequiredForAccess = new ArrayList<>();
        // all urls should be of form "/api/<root path>[path remainder]"
        String [] pathElements = url.split("/");
        if (pathElements.length < 3 || !"api".equals(pathElements[1])) {
            //return Collections.emptyList();
            // this allows super-user to see actuator pages
            authoritiesRequiredForAccess.add(UserPermissionTypes.ROLE_ADMIN);
            return authoritiesRequiredForAccess;
        }
        String keyPath = pathElements[2];
        switch (keyPath) {
            case "user-permissions":
            case "project-permissions":
                // all logged in users can GET their own permissions - enforced in fetch code
                // super user accesses all
                if (HttpMethod.GET == method) {
                    authoritiesRequiredForAccess.add(UserPermissionTypes.ROLE_USER);
                }
                break;

            case "user":
                // super user only
                break;

            default:
                String auth = ApiPermissions.getNonSuperUserAuthorityForUrl(url, method);
                authoritiesRequiredForAccess.add(auth);
        }
        authoritiesRequiredForAccess.add(UserPermissionTypes.ROLE_ADMIN);  // admin can access everything
        return authoritiesRequiredForAccess;
    }

    private UUID getProjectIdFromUrl(String url) {
        // NOTE: all API requests requiring project permissions must have "/pid/" immediately before
        // the project id - in this way the detection of an api call with a project id can be generalised
        // and not need updating each time there is a change to the api spec thus preventing silent permission fails
        int index = url.indexOf("/pid/");
        UUID projectId;
        if (index > 0) {
            String id = url.substring(index + 5, index + 41);
            projectId = UUID.fromString(id);
        } else {
            projectId = null;
        }
        return projectId;
    }
}

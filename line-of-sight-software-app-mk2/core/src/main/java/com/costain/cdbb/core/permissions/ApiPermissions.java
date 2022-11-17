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

package com.costain.cdbb.core.permissions;

/*
This class provides a data-driven way to define which permissions are required for each api call
There must be an entry in the map for each api call variation
(root api, whether with id(s) or not and http method).

All api changes MUST be reflected in this class and the accompanying integration tests

*/

import java.util.AbstractMap;
import java.util.Map;
import org.springframework.http.HttpMethod;

/**
 Provides a data-driven way to define which permissions are required for each api call.
 */
public class ApiPermissions {
    // ENTITY_ID refers to whether the url includes an identity for the entity of the url
    // apart from /project urls it does not include project ids
    private static final String WITH_ENTITY_ID = ":ID:";
    private static final String NO_ENTITY_ID = ":NO_ID:";
    private static final int ROOT_PATH_INDEX = 2;

    private static final String OO_ROOT = "organisational-objectives";
    private static final String PROJECT_ROOT = "project";
    private static final String PROJECT_IMPORT_ROOT = "project/import";
    private static final String PROJECT_EXPORT_ROOT = "project/export";
    private static final String POO_ROOT = "project-organisational-objectives";
    private static final String FR_ROOT = "functional-requirements";
    private static final String FO_ROOT = "functional-outputs";
    private static final String FIRS_ROOT = "firs";
    private static final String ASSET_DD_ROOT = "asset-data-dictionary";
    private static final String AIRS_ROOT = "airs";
    private static final String FO_DD_ROOT = "functional-output-data-dictionary";
    private static final String ASSET_ROOT = "assets";
    private static final String ME_ROOT = "me";

    private static final Map<String, String> apiPermissionsMap = Map.ofEntries(
        // organisational objectives
        getEntryForApiCall(OO_ROOT, NO_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_OO_ID)),
        getEntryForApiCall(OO_ROOT, NO_ENTITY_ID, HttpMethod.POST, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)),
        getEntryForApiCall(OO_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)),
        getEntryForApiCall(OO_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)),
        //POOs
        getEntryForApiCall(POO_ROOT, NO_ENTITY_ID, HttpMethod.GET, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)),
        getEntryForApiCall(POO_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_POO_ID)),
        getEntryForApiCall(POO_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_POO_ID)),

        //FR
        getEntryForApiCall(FR_ROOT, NO_ENTITY_ID, HttpMethod.GET, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)),
        getEntryForApiCall(FR_ROOT, NO_ENTITY_ID, HttpMethod.POST, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FR_ID)),
        getEntryForApiCall(FR_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FR_ID)),
        getEntryForApiCall(FR_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_FR_ID)),

        // FO
        getEntryForApiCall(FO_ROOT, NO_ENTITY_ID, HttpMethod.GET, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)),
        getEntryForApiCall(FO_ROOT, NO_ENTITY_ID, HttpMethod.POST, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FO_ID)),
        getEntryForApiCall(FO_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FO_ID)),
        getEntryForApiCall(FO_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_FO_ID)),
        getEntryForApiCall(FIRS_ROOT, WITH_ENTITY_ID, HttpMethod.POST, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_IMPORT_DATA_ID)),

        // assets
        getEntryForApiCall(ASSET_ROOT, NO_ENTITY_ID, HttpMethod.GET, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)),

        getEntryForApiCall(ASSET_ROOT, NO_ENTITY_ID, HttpMethod.POST, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_ADD_ASSET_ID)),
        getEntryForApiCall(ASSET_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_ADD_ASSET_ID)),
        getEntryForApiCall(ASSET_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, ProjectPermissionTypes.getAuthorityNameForId(

            ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_ASSET_ID)),
        getEntryForApiCall(AIRS_ROOT, WITH_ENTITY_ID, HttpMethod.POST, ProjectPermissionTypes.getAuthorityNameForId(
            ProjectPermissionTypes.PROJECT_PERMISSION_IMPORT_DATA_ID)),

        // asset dd
        getEntryForApiCall(ASSET_DD_ROOT, NO_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)),
        getEntryForApiCall(ASSET_DD_ROOT, WITH_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)),
        getEntryForApiCall(ASSET_DD_ROOT, NO_ENTITY_ID, HttpMethod.POST, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_IMPORT_ID)),


        // fo dd
        getEntryForApiCall(FO_DD_ROOT, NO_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)),
        getEntryForApiCall(FO_DD_ROOT, WITH_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)),
        getEntryForApiCall(FO_DD_ROOT, NO_ENTITY_ID, HttpMethod.POST, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_IMPORT_ID)),

        // projects
        getEntryForApiCall(PROJECT_ROOT, NO_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)),
        getEntryForApiCall(PROJECT_ROOT, NO_ENTITY_ID, HttpMethod.POST, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)),
        getEntryForApiCall(PROJECT_ROOT, WITH_ENTITY_ID, HttpMethod.PUT, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)),
        getEntryForApiCall(PROJECT_ROOT, WITH_ENTITY_ID, HttpMethod.POST, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)),
        getEntryForApiCall(PROJECT_ROOT, WITH_ENTITY_ID, HttpMethod.DELETE, UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_DELETE_PROJECTS_ID)),
        getEntryForApiCall(PROJECT_IMPORT_ROOT, NO_ENTITY_ID, HttpMethod.POST,
            UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_IMPORT_ID)),
        getEntryForApiCall(PROJECT_EXPORT_ROOT, WITH_ENTITY_ID, HttpMethod.GET,
            UserPermissionTypes.getAuthorityNameForId(
            UserPermissionTypes.USER_PERMISSION_EXPORT_ID)),

        // me
        getEntryForApiCall(ME_ROOT, NO_ENTITY_ID, HttpMethod.GET, UserPermissionTypes.ROLE_USER)
    );

    private static AbstractMap.SimpleEntry<String, String> getEntryForApiCall(
        String url, String idStr, HttpMethod method, String authority) {
        return new AbstractMap.SimpleEntry<String, String>(url + idStr + method.toString(), authority);
    }

    /**
     * Get the authority (permission) a non-super user requires to access the url with the given http method.
     * @param url url for api access
     * @param method http method used with url
     * @return non-super user permission required to access the url with the http method
     */
    public static String getNonSuperUserAuthorityForUrl(String url, HttpMethod method) {
        String [] pathElements = url.split("/");
        // some roots have 2 path elements e.g. project/import, most have 1 - start with potential for 2
        String result = null;
        if (pathElements.length > ROOT_PATH_INDEX + 1) {
            result = apiPermissionsMap.get(pathElements[ROOT_PATH_INDEX] + "/" + pathElements[ROOT_PATH_INDEX + 1]
                + idStr(url, ROOT_PATH_INDEX + 1) + method.toString());
        }
        if (result == null) {
            result = apiPermissionsMap.get(pathElements[ROOT_PATH_INDEX]
                + idStr(url, ROOT_PATH_INDEX) + method.toString());
        }
        return result;
    }


    private static String idStr(String url, int lastRootPathIndex) {
        // see if non-project entity id present
        String [] pathElements = url.split("/");
        for (String path : pathElements) {
            if ("pid".equals(path) && !"project".equals(pathElements[ROOT_PATH_INDEX])) {
                return pathElements.length > (ROOT_PATH_INDEX + 3) ? WITH_ENTITY_ID : NO_ENTITY_ID;
            }
        }
        return pathElements.length > (lastRootPathIndex + 1) ? WITH_ENTITY_ID : NO_ENTITY_ID;
    }
}

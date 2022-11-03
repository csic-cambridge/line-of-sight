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

import java.util.AbstractMap;
import java.util.Map;

public class UserPermissionTypes {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final int USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID = 1010;
    public static final int USER_PERMISSION_EDIT_PROJECTS_ID = 1011;
    public static final int USER_PERMISSION_DELETE_PROJECTS_ID = 1012;
    public static final int USER_PERMISSION_IMPORT_ID = 1110;
    public static final int USER_PERMISSION_EXPORT_ID = 1111;
    public static final int USER_PERMISSION_VIEW_OO_ID = 1210;
    public static final int USER_PERMISSION_EDIT_OO_OIR_ID = 1211;
    public static final int USER_PERMISSION_EDIT_DATA_DICTIONARY_ID = 1311;

    public static final Map<Integer, String> userPermissionTypes = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID, "View projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_PROJECTS_ID,
            "Add, copy and rename projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_DELETE_PROJECTS_ID, "Delete projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_IMPORT_ID, "Import projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EXPORT_ID, "Export projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_VIEW_OO_ID, "View organisational objectives(OO)"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_OO_OIR_ID, "Edit organisational objectives(OO)"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_DATA_DICTIONARY_ID, "Edit data dictionaries")
        );


    public static String getPermissionNameForId(Integer userPermissionId) {
        String name = userPermissionTypes.get(userPermissionId);
        return name == null ? "Unknown permission " + userPermissionId : name;
    }

    public static String getAuthorityNameForId(Integer userPermissionId) {
        return "USER_PERMISSION-" + userPermissionId;
    }
}


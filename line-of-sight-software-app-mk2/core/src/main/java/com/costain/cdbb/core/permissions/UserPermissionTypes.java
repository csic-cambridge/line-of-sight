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

    public static final UserPermissionId USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID = new UserPermissionId(1010);
    public static final UserPermissionId USER_PERMISSION_EDIT_PROJECTS_ID = new UserPermissionId(1011);
    public static final UserPermissionId USER_PERMISSION_DELETE_PROJECTS_ID = new UserPermissionId(1012);
    public static final UserPermissionId USER_PERMISSION_IMPORT_ID = new UserPermissionId(1110);
    public static final UserPermissionId USER_PERMISSION_EXPORT_ID = new UserPermissionId(1111);
    public static final UserPermissionId USER_PERMISSION_VIEW_OO_ID = new UserPermissionId(1210);
    public static final UserPermissionId USER_PERMISSION_EDIT_OO_OIR_ID = new UserPermissionId(1211);
    public static final UserPermissionId USER_PERMISSION_EDIT_DATA_DICTIONARY_ID = new UserPermissionId(1311);

    public static final Map<Integer, String> userPermissionTypes = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID.getId(), "View projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_PROJECTS_ID.getId(),
            "Add, copy and rename projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_DELETE_PROJECTS_ID.getId(),
            "Delete projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_IMPORT_ID.getId(), "Import projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EXPORT_ID.getId(), "Export projects in projects dashboard"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_VIEW_OO_ID.getId(), "View organisational objectives(OO)"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_OO_OIR_ID.getId(), "Edit organisational objectives(OO)"),
        new AbstractMap.SimpleEntry<>(USER_PERMISSION_EDIT_DATA_DICTIONARY_ID.getId(), "Edit data dictionaries")
        );


    public static String getPermissionNameForId(UserPermissionId userPermissionId) {
        String name = userPermissionTypes.get(userPermissionId.getId());
        return name == null ? "Unknown permission " + userPermissionId.getId() : name;
    }

    public static String getAuthorityNameForId(UserPermissionId userPermissionId) {
        return "USER_PERMISSION-" + userPermissionId.getId();
    }
}


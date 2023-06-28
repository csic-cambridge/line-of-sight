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

/**
 * Defines project permissions.
 */
public class ProjectPermissionTypes {
    public static final ProjectPermissionId PROJECT_PERMISSION_VIEW_PROJECT_ID = new ProjectPermissionId(2010);
    public static final ProjectPermissionId PROJECT_PERMISSION_EDIT_POO_ID = new ProjectPermissionId(2110);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_POO_ID = new ProjectPermissionId(2111);
    public static final ProjectPermissionId PROJECT_PERMISSION_LINK_OIR_AIR = new ProjectPermissionId(2112);

    public static final ProjectPermissionId PROJECT_PERMISSION_EDIT_FR_ID = new ProjectPermissionId(2210);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_FR_ID = new ProjectPermissionId(2211);

    public static final ProjectPermissionId PROJECT_PERMISSION_EDIT_FO_ID = new ProjectPermissionId(2310);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_FO_ID = new ProjectPermissionId(2311);
    public static final ProjectPermissionId PROJECT_PERMISSION_ADD_FIR_ID = new ProjectPermissionId(2315);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_FIR_ID = new ProjectPermissionId(2316);

    public static final ProjectPermissionId PROJECT_PERMISSION_ADD_ASSET_ID = new ProjectPermissionId(2410);
    public static final ProjectPermissionId PROJECT_PERMISSION_ADD_AIR_ID = new ProjectPermissionId(2411);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_AIR_ID = new ProjectPermissionId(2412);
    public static final ProjectPermissionId PROJECT_PERMISSION_DELETE_ASSET_ID = new ProjectPermissionId(2413);

    public static final ProjectPermissionId PROJECT_PERMISSION_IMPORT_DATA_ID = new ProjectPermissionId(2510);

    public static final Map<Integer, String> projectPermissionTypes = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_VIEW_PROJECT_ID.getId(), "View project graph"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_POO_ID.getId(),
            "Edit project organisational objectives(POO)"),
        new AbstractMap.SimpleEntry<>(
        PROJECT_PERMISSION_DELETE_POO_ID.getId(), "Delete project organisational objectives(POO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_LINK_OIR_AIR.getId(), "Link OIR with AIR"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_FR_ID.getId(), "Edit functional requirements(FR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_FR_ID.getId(), "Delete functional requirements(FR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_FO_ID.getId(), "Edit functional outputs(FO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_FO_ID.getId(), "Delete functional outputs(FO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_ADD_FIR_ID.getId(),
            "Add functional information requirements (FIR)"),
        new AbstractMap.SimpleEntry<>(
        PROJECT_PERMISSION_DELETE_FIR_ID.getId(), "Delete functional information requirements (FIR)"),
        new AbstractMap.SimpleEntry<>(

        PROJECT_PERMISSION_ADD_ASSET_ID.getId(), "Add assets"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_ADD_AIR_ID.getId(), "Add asset information requirements(AIR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_AIR_ID.getId(),
            "Delete asset information requirements(AIR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_ASSET_ID.getId(), "Delete assets"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_IMPORT_DATA_ID.getId(), "Import Data")
        );


    public static String getPermissionNameForId(ProjectPermissionId projectPermissionId) {
        String name = projectPermissionTypes.get(projectPermissionId.getId());
        return name == null ? "Unknown permission " + projectPermissionId.getId() : name;
    }

    public static String getAuthorityNameForId(ProjectPermissionId projectPermissionId) {
        return "PROJECT_PERMISSION-" + projectPermissionId.getId();
    }
}

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

public class ProjectPermissionTypes {
    public static final int PROJECT_PERMISSION_VIEW_PROJECT_ID = 2010;
    public static final int PROJECT_PERMISSION_EDIT_POO_ID = 2110;
    public static final int PROJECT_PERMISSION_DELETE_POO_ID = 2111;

    public static final int PROJECT_PERMISSION_EDIT_FR_ID = 2210;
    public static final int PROJECT_PERMISSION_DELETE_FR_ID = 2211;

    public static final int PROJECT_PERMISSION_EDIT_FO_ID = 2310;
    public static final int PROJECT_PERMISSION_DELETE_FO_ID = 2311;
    public static final int PROJECT_PERMISSION_ADD_FIR_ID = 2315;
    public static final int PROJECT_PERMISSION_DELETE_FIR_ID = 2316;

    public static final int PROJECT_PERMISSION_ADD_ASSET = 2410;
    public static final int PROJECT_PERMISSION_ADD_AIR_ID = 2411;
    public static final int PROJECT_PERMISSION_DELETE_AIR_ID = 2412;
    public static final int PROJECT_PERMISSION_DELETE_ASSET_ID = 2413;

    public static final int PROJECT_PERMISSION_IMPORT_DATA_ID = 2510;

    public static final Map<Integer, String> projectPermissionTypes = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_VIEW_PROJECT_ID, "View project graph"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_POO_ID, "Edit project organisational objectives(POO)"),
        new AbstractMap.SimpleEntry<>(
        PROJECT_PERMISSION_DELETE_POO_ID, "Delete project organisational objectives(POO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_FR_ID, "Edit functional requirements(FR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_FR_ID, "Delete functional requirements(FR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_EDIT_FO_ID, "Edit functional outputs(FO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_FO_ID, "Delete functional outputs(FO)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_ADD_FIR_ID, "Add functional information requirements (FIR)"),
        new AbstractMap.SimpleEntry<>(
        PROJECT_PERMISSION_DELETE_FIR_ID, "Delete functional information requirements (FIR)"),
        new AbstractMap.SimpleEntry<>(
            PROJECT_PERMISSION_ADD_ASSET, "Add assets"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_ADD_AIR_ID, "Add asset information requirements(AIR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_AIR_ID, "Delete asset information requirements(AIR)"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_DELETE_ASSET_ID, "Delete assets"),
        new AbstractMap.SimpleEntry<>(PROJECT_PERMISSION_IMPORT_DATA_ID, "Import Data")
        );


    public static String getPermissionNameForId(Integer projectPermissionId) {
        String name = projectPermissionTypes.get(projectPermissionId);
        return name == null ? "Unknown permission " + projectPermissionId : name;
    }

    public static String getAuthorityNameForId(Integer projectPermissionId) {
        return "PROJECT_PERMISSION-" + projectPermissionId;
    }
}

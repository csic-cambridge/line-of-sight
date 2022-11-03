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

import com.costain.cdbb.model.PermissionType;
import java.io.Serializable;
import java.util.Comparator;

public class PermissionsComparator implements Comparator<PermissionType>, Serializable {
    private static final long serialVersionUID = 1;

    @Override
    public int compare(PermissionType pt1, PermissionType pt2) {
        return pt1.getId().compareTo(pt2.getId());
    }
}

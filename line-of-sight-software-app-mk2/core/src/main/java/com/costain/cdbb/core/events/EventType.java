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

package com.costain.cdbb.core.events;

/**
 * Events which cause notification to be sent to all front-end users.
 */
public enum EventType {
    USER_PERMISSION_CHANGED(100),
    PROJECT_PERMISSION_CHANGED(200),
    OOS_CHANGED(300),
    PROJECT_DELETED(400),
    PROJECT_ADDED(410),
    PROJECT_RENAMED(420),
    PROJECT_ENTITIES_CHANGED(510);

    private int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

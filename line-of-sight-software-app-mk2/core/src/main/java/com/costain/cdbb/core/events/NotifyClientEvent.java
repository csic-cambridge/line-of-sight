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

import org.springframework.context.ApplicationEvent;

/**
 * Application event for when client to be notified of change.
 */
public class NotifyClientEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1;

    public NotifyClientEvent(ClientNotification cn) {
        super(cn);
    }

    public String toJson() {
        return ((ClientNotification)super.getSource()).toJson();
    }
}

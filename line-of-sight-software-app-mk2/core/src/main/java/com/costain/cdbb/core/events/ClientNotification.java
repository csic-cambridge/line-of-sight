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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * Manages the notifications to front-end when defined changes have occurred.
 */
public class ClientNotification {

    private ObjectMapper lazyObjectMapper = null;

    private UUID id;
    private EventType eventType;
    private UUID userId;
    private UUID projectId;

    public ClientNotification(EventType eventType) {
        id = UUID.randomUUID();
        this.eventType = eventType;
        this.userId = null;
        this.projectId = null;
    }

    public ClientNotification(EventType eventType, UUID userId, UUID projectId) {
        this(eventType);
        this.userId = userId;
        this.projectId = projectId;
    }

    public ClientNotification(EventType eventType, UUID userId) {
        this(eventType);
        this.userId = userId;
        this.projectId = null;
    }

    public UUID getId() {
        return id;
    }

    public EventType geteventType() {
        return eventType;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    private ObjectMapper getObjectMapper() {
        if (this.lazyObjectMapper == null) {
            this.lazyObjectMapper = new ObjectMapper();
        }
        return this.lazyObjectMapper;
    }

    public String toString() {
        return "{ClientNotification: id= " + id.toString()
            + ", eventType=" + eventType
            + ", userId=" + (userId == null ? "n/a" : userId.toString())
            + ", projectId=" + (projectId == null ? "n/a" : projectId.toString())
            + "}";
    }

    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("type", this.eventType.getId());
        map.put("userId", this.userId);
        if (projectId != null) {
            map.put("projectId", projectId.toString());
        }
        try {
            return this.getObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "Failed to read event data" + e;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientNotification)) {
            return false;
        }
        ClientNotification other = (ClientNotification) o;
        return eventType == other.geteventType()
            && userId.toString().equals(other.getUserId().toString())
            && id.toString().equals(other.getId().toString())
            && (
            projectId == null || other.getProjectId() == null
                    ? projectId == null && other.getProjectId() == null
                    : projectId.toString().equals(other.getProjectId().toString())
            );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType, userId, projectId);
    }
}

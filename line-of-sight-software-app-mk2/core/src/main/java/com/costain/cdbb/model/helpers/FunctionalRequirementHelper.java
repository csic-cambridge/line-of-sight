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

import com.costain.cdbb.model.FunctionalObjectiveDAO;
import com.costain.cdbb.model.FunctionalRequirement;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.FunctionalRequirementWithId;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalRequirementHelper {

    public FunctionalRequirementWithId fromDao(FunctionalRequirementDAO dao) {
        FunctionalRequirementWithId dto = new FunctionalRequirementWithId();
        dto.id(dao.getId());
        dto.name(dao.getName());
        dto.fos(dao.getFos() != null ? dao.getFos().stream().map(fo -> fo.getId()).toList()
            : Collections.emptyList());

        return dto;
    }

    public FunctionalRequirementDAO fromDto(FunctionalRequirement functionalRequirement) {
        return fromDto(FunctionalRequirementDAO.builder(), functionalRequirement.getName(),
            functionalRequirement.getFos());
    }

    public FunctionalRequirementDAO fromDto(UUID id, FunctionalRequirement functionalRequirement) {
        return fromDto(FunctionalRequirementDAO.builder().id(id), functionalRequirement.getName(),
            functionalRequirement.getFos());
    }

    private FunctionalRequirementDAO fromDto(FunctionalRequirementDAO.FunctionalRequirementDAOBuilder builder,
            String name, Collection<UUID> fos) {
        return builder
            .name(name)
            .fos(fos.stream().map(id -> FunctionalObjectiveDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
    }
}

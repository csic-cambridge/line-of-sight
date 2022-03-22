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

import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OrganisationalObjective;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.OrganisationalObjectiveWithId;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OriganisationalObjectiveHelper {

    public OrganisationalObjectiveWithId fromDao(OrganisationalObjectiveDAO dao) {
        OrganisationalObjectiveWithId dto = new OrganisationalObjectiveWithId().id(dao.getId()).name(dao.getName());
        dto.frs(dao.getFrs() != null
            ? dao.getFrs().stream().map(fr -> fr.getId()).toList()
            : Collections.emptyList());
        dto.oirs(dao.getOirs() != null ? dao.getOirs().stream().toList() : Collections.emptyList());
        return dto;
    }

    public OrganisationalObjectiveDAO fromDto(OrganisationalObjective dto) {
        return fromDto(OrganisationalObjectiveDAO.builder(), dto.getName(), dto.getFrs(), dto.getOirs());
    }

    public OrganisationalObjectiveDAO fromDto(UUID id, OrganisationalObjective dto) {
        return fromDto(OrganisationalObjectiveDAO.builder().id(id), dto.getName(), dto.getFrs(), dto.getOirs());
    }

    private OrganisationalObjectiveDAO fromDto(
            OrganisationalObjectiveDAO.OrganisationalObjectiveDAOBuilder daoBuilder, String name, Collection<UUID> frs,
            Collection<String> oirs) {

        daoBuilder.name(name).frs(frs != null
            ? frs.stream().map(uuid -> FunctionalRequirementDAO.builder().id(uuid).build()).collect(Collectors.toSet())
            : Collections.emptySet());
        daoBuilder.oirs(oirs != null ? oirs.stream().collect(Collectors.toSet()) : Collections.emptySet());
        return daoBuilder.build();
    }
}

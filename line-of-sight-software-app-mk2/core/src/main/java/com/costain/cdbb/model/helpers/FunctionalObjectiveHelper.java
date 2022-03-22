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

import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.FunctionalObjective;
import com.costain.cdbb.model.FunctionalObjectiveDAO;
import com.costain.cdbb.model.FunctionalObjectiveDataDictionaryEntry;
import com.costain.cdbb.model.FunctionalObjectiveDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalObjectiveWithId;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalObjectiveHelper {

    public FunctionalObjectiveWithId fromDao(FunctionalObjectiveDAO dao) {
        FunctionalObjectiveWithId dto = new FunctionalObjectiveWithId();
        dto.id(dao.getId());
        FunctionalObjectiveDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new FunctionalObjectiveDataDictionaryEntry().id(dde.getId()).text(dde.getText()));
        dto.firs(dao.getFirs() != null ? dao.getFirs().stream().toList() : Collections.emptyList());
        dto.assets(dao.getAssets() != null ? dao.getAssets().stream().map(asset -> asset.getId()).toList()
            : Collections.emptyList());

        return dto;
    }

    public FunctionalObjectiveDAO fromDto(FunctionalObjective functionalObjective) {
        return fromDto(FunctionalObjectiveDAO.builder(), functionalObjective.getDataDictionaryEntry(),
            functionalObjective.getFirs(), functionalObjective.getAssets());
    }

    public FunctionalObjectiveDAO fromDto(UUID id, FunctionalObjective functionalObjective) {
        return fromDto(FunctionalObjectiveDAO.builder().id(id), functionalObjective.getDataDictionaryEntry(),
            functionalObjective.getFirs(), functionalObjective.getAssets());
    }

    private FunctionalObjectiveDAO fromDto(FunctionalObjectiveDAO.FunctionalObjectiveDAOBuilder builder,
            FunctionalObjectiveDataDictionaryEntry dde, Collection<String> firs, Collection<UUID> assets) {
        return builder
            .dataDictionaryEntry(FunctionalObjectiveDataDictionaryEntryDAO.builder().id(dde.getId()).text(dde.getText())
                .build())
            .firs(firs.stream().collect(Collectors.toSet()))
            .assets(assets.stream().map(id -> AssetDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
    }
}

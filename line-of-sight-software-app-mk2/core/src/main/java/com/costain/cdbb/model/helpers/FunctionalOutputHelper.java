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
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.model.FunctionalOutput;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputWithId;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalOutputHelper {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository ddeRepository;

    public FunctionalOutputWithId fromDao(FunctionalOutputDAO dao) {
        FunctionalOutputWithId dto = new FunctionalOutputWithId();
        dto.id(dao.getId());
        FunctionalOutputDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new DataDictionaryEntry()
            .id(dde.getId())
            .text(dde.getId() + "-" + dde.getText()));
        dto.firs(dao.getFirs() != null ? dao.getFirs().stream().toList() : Collections.emptyList());
        dto.assets(dao.getAssets() != null ? dao.getAssets().stream().map(AssetDAO::getId).toList()
            : Collections.emptyList());
        return dto;
    }

    public FunctionalOutputDAO inputFromDto(UUID projectId, UUID foId, FunctionalOutput fo) {
        return fromDto(FunctionalOutputDAO.builder().id(foId),
            projectId,
            fo.getDataDictionaryEntry().getId(),
            fo.getFirs(),
            fo.getAssets());
    }

    public FunctionalOutputDAO fromDto(FunctionalOutputWithId functionalOutput, UUID projectId) {
        return fromDto(FunctionalOutputDAO.builder(),
            projectId,
            functionalOutput.getDataDictionaryEntry().getId(),
            functionalOutput.getFirs(),
            functionalOutput.getAssets());
    }

    public FunctionalOutputDAO fromDto(UUID id, FunctionalOutputWithId functionalOutput, UUID projectId) {
        return fromDto(FunctionalOutputDAO.builder().id(id),
            projectId,
            functionalOutput.getDataDictionaryEntry().getId(),
            functionalOutput.getFirs(), functionalOutput.getAssets());
    }

    private FunctionalOutputDAO fromDto(FunctionalOutputDAO.FunctionalOutputDAOBuilder builder,
                                        UUID projectId,
                                        String ddeId, Collection<String> firs, Collection<UUID> assets) {
        Optional<FunctionalOutputDataDictionaryEntryDAO> optFoDdeDao = ddeRepository.findById(ddeId);
        return builder
            .projectId(projectId)
            .dataDictionaryEntry(optFoDdeDao.get())
            .firs(new HashSet<>(firs))
            .assets(assets.stream().map(id -> AssetDAO.builder().id(id).build()).collect(Collectors.toSet()))
            .build();
    }
}

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

import com.costain.cdbb.model.Asset;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.AssetWithId;
import com.costain.cdbb.model.DataDictionaryEntry;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetHelper {
    @Autowired
    AssetDataDictionaryEntryRepository ddeRepository;

    @Autowired
    ProjectRepository projectRepository;

    public AssetWithId fromDao(AssetDAO dao) {
        AssetWithId dto = new AssetWithId();
        dto.id(dao.getId());
        AssetDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new DataDictionaryEntry()
            .id(dde.getId())
            .text(dde.getId() + "-" + dde.getText()));
        dto.airs(dao.getAirs() != null ? dao.getAirs().stream().toList() : Collections.emptyList());
        return dto;
    }

    public AssetDAO fromDto(UUID id, Asset asset, UUID projectId) {
        return fromDto(AssetDAO.builder().id(id), projectId,
            asset.getDataDictionaryEntry().getId(), asset.getAirs());
    }

    public AssetDAO fromDto(AssetWithId asset, UUID projectId) {
        return fromDto(AssetDAO.builder(), projectId,
            asset.getDataDictionaryEntry().getId(), asset.getAirs());
    }

    public AssetDAO fromDto(UUID id, AssetWithId asset, UUID projectId) {
        return fromDto(AssetDAO.builder().id(id), projectId,
            asset.getDataDictionaryEntry().getId(), asset.getAirs());
    }

    private AssetDAO fromDto(AssetDAO.AssetDAOBuilder builder, UUID projectId,
                             String ddeId, Collection<String> airs) {
        Optional<AssetDataDictionaryEntryDAO> optAssetDdeDao = ddeRepository.findById(ddeId);
        return builder
            .projectId(projectId)
            .dataDictionaryEntry(optAssetDdeDao.get())
            .airs(airs != null ? new HashSet<>(airs) : Collections.emptySet())
            .build();
    }

}

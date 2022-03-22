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
import com.costain.cdbb.model.AssetDataDictionaryEntry;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.AssetWithId;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetHelper {

    public AssetWithId fromDao(AssetDAO dao) {
        AssetWithId dto = new AssetWithId();
        dto.id(dao.getId());
        AssetDataDictionaryEntryDAO dde = dao.getDataDictionaryEntry();
        dto.dataDictionaryEntry(new AssetDataDictionaryEntry().id(dde.getId()).text(dde.getText()));
        dto.airs(dao.getAirs() != null ? dao.getAirs().stream().toList() : Collections.emptyList());

        return dto;
    }

    public AssetDAO fromDto(Asset asset) {
        return fromDto(AssetDAO.builder(), asset.getDataDictionaryEntry(), asset.getAirs());
    }

    public AssetDAO fromDto(UUID id, Asset asset) {
        return fromDto(AssetDAO.builder().id(id), asset.getDataDictionaryEntry(), asset.getAirs());
    }

    private AssetDAO fromDto(AssetDAO.AssetDAOBuilder builder, AssetDataDictionaryEntry dde, Collection<String> airs) {
        return builder
            .dataDictionaryEntry(AssetDataDictionaryEntryDAO.builder().id(dde.getId()).text(dde.getText()).build())
            .airs(airs != null ? airs.stream().collect(Collectors.toSet()) : Collections.emptySet())
            .build();
    }

}

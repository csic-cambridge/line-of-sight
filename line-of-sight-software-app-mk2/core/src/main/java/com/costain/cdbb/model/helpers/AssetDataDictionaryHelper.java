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

import com.costain.cdbb.model.AssetDataDictionary;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides helper functions for managing and manipulating asset data dictionaries.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AssetDataDictionaryHelper {

    /**
     * Create an asset dto from an asset dao.
     * @param dao the source data
     * @return AssetDataDictionary the created dto.
     */
    public AssetDataDictionary fromDao(AssetDataDictionaryDAO dao) {
        AssetDataDictionary dto = new AssetDataDictionary();
        dto.id(dao.getId());
        dto.name(dao.getName());
        return dto;
    }

    /**
     * Create AssetDataDictionaryDAO for a new asset from an asset dto.
     * @param assetDataDictionary the source data
     * @return AssetDataDictionaryDAO the created dao
     */
    public AssetDataDictionaryDAO fromDto(AssetDataDictionary assetDataDictionary) {
        return fromDto(AssetDataDictionaryDAO.builder(), assetDataDictionary.getName());
    }

    /**
     * Create AssetDataDictionaryDAO for an existing asset from an asset dto.
     * @param id the asset id
     * @param assetDataDictionary the source data
     * @return AssetDataDictionaryDAO the created dao
     */
    public AssetDataDictionaryDAO fromDto(UUID id, AssetDataDictionary assetDataDictionary) {
        return fromDto(AssetDataDictionaryDAO.builder().id(id), assetDataDictionary.getName());
    }

    private AssetDataDictionaryDAO fromDto(AssetDataDictionaryDAO.AssetDataDictionaryDAOBuilder builder, String name) {
        return builder.name(name).build();
    }
}

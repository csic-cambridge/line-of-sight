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


import com.costain.cdbb.model.FunctionalOutputDataDictionary;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FunctionalOutputDataDictionaryHelper {

    public FunctionalOutputDataDictionary fromDao(FunctionalOutputDataDictionaryDAO dao) {
        FunctionalOutputDataDictionary dto = new FunctionalOutputDataDictionary();
        dto.id(dao.getId());
        dto.name(dao.getName());
        return dto;
    }

    public FunctionalOutputDataDictionaryDAO fromDto(FunctionalOutputDataDictionary foDataDictionary) {
        return fromDto(FunctionalOutputDataDictionaryDAO.builder(), foDataDictionary.getName());
    }

    public FunctionalOutputDataDictionaryDAO fromDto(UUID id, FunctionalOutputDataDictionary foDataDictionary) {
        return fromDto(FunctionalOutputDataDictionaryDAO.builder().id(id), foDataDictionary.getName());
    }

    private FunctionalOutputDataDictionaryDAO fromDto(
        FunctionalOutputDataDictionaryDAO.FunctionalOutputDataDictionaryDAOBuilder builder, String name) {
        return builder.name(name).build();
    }
}

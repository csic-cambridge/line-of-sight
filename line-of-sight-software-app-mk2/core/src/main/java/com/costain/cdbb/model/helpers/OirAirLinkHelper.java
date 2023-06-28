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

import com.costain.cdbb.core.CdbbValidationError;
import com.costain.cdbb.core.events.ClientNotification;
import com.costain.cdbb.core.events.EventType;
import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.OirRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * Provides helper functions for managing and manipulating assets.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OirAirLinkHelper {

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    OirRepository oirRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void linkOirToAir(boolean linkunlink, UUID projectId, String jsonIds) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonIds);
        String oirId = jsonObject.getString("oirId");
        String airId = jsonObject.getString("airId");
        if (null == oirId || null == airId) {
            throw new CdbbValidationError("Must specify string values for oirId and airId keys");
        }
        // make sure oir and air belong to this project and are linked through fr,fo and assets
        OirDAO matchedOirDao = pooRepository.findByProjectIdAndOoVersionOoIsDeletedFalse(projectId).stream()
            .flatMap(pooDao -> pooDao.getOoVersion().getOo().getOirDaos().stream())
            .filter(oirDao -> oirId.equals(oirDao.getId().toString()))
            .findFirst()
            .orElse(null);
        AirsDAO matchedAirsDao = assetRepository.findByProjectId(projectId).stream()
            .flatMap(assetDao -> assetDao.getAirs().stream())
            .filter(airsDao -> airId.equals(airsDao.getId().toString()))
            .findFirst()
            .orElse(null);

        AirsDAO airFromOir = null;
        if (null != matchedOirDao) {
            airFromOir =
                pooRepository.findByProjectIdAndOoVersionOoIdAndOoVersionOoIsDeletedFalse(
                    projectId, UUID.fromString(matchedOirDao.getOoId())).stream()
                    .flatMap(pooDao -> pooDao.getFrs().stream())
                    .flatMap(frDao -> frDao.getFos().stream())
                    .flatMap(foDao -> foDao.getAssets().stream())
                    .flatMap(assetDao -> assetDao.getAirs().stream())
                    .filter(airsDao -> airsDao.getId().toString().equals(airId))
                    .findFirst()
                    .orElse(null);
        }
        if (null == matchedOirDao || null == matchedAirsDao) {
            throw new CdbbValidationError("Must use oirId and airId in the specified project");
        } else if (null == airFromOir) {
            throw new CdbbValidationError("oirId and airId must be linked through FR, FO and Asset entities");
        }
        if (linkunlink) {
            // now link the two together
            matchedOirDao.getAirs().add(matchedAirsDao);
        } else {
            // remove link (if it exists)
            matchedOirDao.getAirs().remove(matchedAirsDao);
        }

        oirRepository.save(matchedOirDao);
        applicationEventPublisher.publishEvent(
            new NotifyClientEvent(new ClientNotification(EventType.PROJECT_ENTITIES_CHANGED, null, null)));
    }
}

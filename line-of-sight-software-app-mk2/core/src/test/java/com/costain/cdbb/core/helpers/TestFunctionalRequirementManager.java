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

package com.costain.cdbb.core.helpers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestFunctionalRequirementManager {
    private Set<String> sourceFos;
    private Set<FunctionalOutputDAO> fos;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestFunctionalOutputManager foManager;

    public Set<String> getSourceFos() {
        return sourceFos;
    }

    public void setSourceFos(Set<String> sourceFos) {
        this.sourceFos = sourceFos;
    }

    public FunctionalRequirementDAO createFunctionalRequirement(UUID projectId, int port) throws JSONException {
        FunctionalOutputDAO fo = foManager.createFunctionalOutput(projectId, port);
        sourceFos = new HashSet<>();
        sourceFos.add(fo.getId().toString());
        fos = new HashSet<>();
        fos.add(fo);

        String frName = "Test FR " + System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        map.put("name", frName);
        map.put("fos", sourceFos.toArray());
        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/functional-requirements/" + projectId);
        // build FunctionalRequirementDAO result from response
        String frResultAsJsonStr = response.getBody();
        JSONObject frJsonObject = new JSONObject(frResultAsJsonStr);
        JSONArray fosJsonArray = frJsonObject.getJSONArray("fos");
        Set<String> responseFos = new HashSet<>();
        for (int i = 0; i < fosJsonArray.length(); i++) {
            responseFos.add((String)fosJsonArray.get(i));
        }
        FunctionalRequirementDAO result = FunctionalRequirementDAO.builder()
            .id(UUID.fromString(frJsonObject.getString("id")))
            .name(frJsonObject.getString("name"))
            .projectId(UUID.fromString(frJsonObject.getString("project_id")))
            .build();
        assertAll(
            () -> assertEquals(result.getProjectId(), projectId),
            () -> assertEquals(result.getName(), frName),
            () -> assertEquals(sourceFos.size(), 1),
            () -> assertTrue(responseFos.containsAll(sourceFos) && sourceFos.containsAll(responseFos))
        );
        return result;
    }

    public void deleteFunctionalRequirement(UUID projectId, FunctionalRequirementDAO fr, int port) {
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/functional-requirements/" + projectId + "/" + fr.getId());
        // check it has been deleted from database
        assertFalse(frRepository.findById(fr.getId()).isPresent());

        if (fos != null && fos.size() > 0) {
            for (FunctionalOutputDAO fo : fos) {
                foManager.deleteFunctionalOutput(projectId, port, fo);
            }
        }
    }
}

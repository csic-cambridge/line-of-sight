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

package com.costain.cdbb.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestFunctionalOutputManager;
import com.costain.cdbb.core.helpers.TestFunctionalRequirementManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;



@ActiveProfiles("no_security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FunctionalRequirementsRestTest {
    @LocalServerPort
    private int port;
    private static UUID projectId;
    private static FunctionalOutputDataDictionaryDAO foDdDao;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestFunctionalOutputManager foManager;

    @Autowired
    TestFunctionalRequirementManager frManager;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            foDdDao = foManager.createFoDdWithEntry(projectId, port);
            projectId = projectManager.create("Functional Requirement Test", foDdDao.getId(), null, port);
        } catch (Exception e) {
            fail("Failed to create initial project" + e);
        }
    }

    @BeforeEach
    public void runBeforeAllTestMethods() {

    }

    @AfterAll
    public void runAfterAllTestsComplete() {
        try {
            projectManager.delete(projectId, port);
            foManager.deleteFunctionalOutputDictionary(foDdDao.getId());
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
    }

    @Test
    public void getAllFunctionalRequirements() {
        FunctionalRequirementDAO fr = null;
        try {
            fr = frManager.createFunctionalRequirement(projectId, port);

            HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/functional-requirements/pid/" + projectId);
            // process result when no entries
            String frResultAsJsonStr = response.getBody();
            System.out.println("Get all Functional Requirements response: " + frResultAsJsonStr);
            try {
                JSONArray frJsonArray = new JSONArray(frResultAsJsonStr);
                List<FunctionalRequirementDAO> responseFrs = new ArrayList<>();
                assertEquals(frJsonArray.length(), 1,
                    "Unexpected number of functional requirements found");
                for (int i = 0; i < frJsonArray.length(); i++) {
                    JSONObject frJsonObject = (JSONObject) frJsonArray.get(i);
                    JSONArray fosJsonArray = frJsonObject.getJSONArray("fos");
                    Set<String> responseFos = new HashSet<>();
                    for (int j = 0; j < fosJsonArray.length(); j++) {
                        responseFos.add((String) fosJsonArray.get(j));
                    }
                    FunctionalRequirementDAO result = FunctionalRequirementDAO.builder()
                        .id(UUID.fromString(frJsonObject.getString("id")))
                        .name(frJsonObject.getString("name"))
                        .projectId(UUID.fromString(frJsonObject.getString("project_id")))
                        .build();

                    FunctionalRequirementDAO finalFr = fr;
                    assertAll(
                        () -> assertEquals(result.getProjectId(), projectId),
                        () -> assertEquals(result.getName(), finalFr.getName()),
                        () -> assertEquals(responseFos.size(), 1),
                        () -> assertTrue(responseFos.containsAll(frManager.getSourceFos())
                            && frManager.getSourceFos().containsAll(responseFos))
                    );
                }
            } catch (Exception e) {
                fail(e);
            }
        } catch (Exception e) {
            fail(e);
        } finally {
            if (fr != null) {
                frManager.deleteFunctionalRequirement(projectId, fr, port);
            }
        }
    }

    // Note: getFunctionalRequirement() is not part of the api

    @Test
    public void updateFunctionalRequirement() {
        // can update the name and FO
        FunctionalRequirementDAO fr = null;
        FunctionalOutputDAO fo = null;
        try {
            fr = frManager.createFunctionalRequirement(projectId, port);
            fo = foManager.createFunctionalOutput(projectId, port);
            // now change the airs and do an update
            frManager.setSourceFos(new HashSet<>());
            frManager.getSourceFos().add(fo.getId().toString());

            String frName = "Test Renamed FR " + System.currentTimeMillis();
            Map<String, Object> map = new HashMap<>();
            map.put("project_id", projectId);
            map.put("name", frName);
            map.put("fos", frManager.getSourceFos().toArray());
            String payload = new ObjectMapper().writeValueAsString(map);
            ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
                payload,
                "http://localhost:" + port + "/api/functional-requirements/pid/" + projectId + "/" + fr.getId());
            // process result
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
                () -> assertEquals(frManager.getSourceFos().size(), 1),
                () -> assertTrue(responseFos.containsAll(frManager.getSourceFos())
                    && frManager.getSourceFos().containsAll(responseFos))
            );
        } catch (JSONException | JsonProcessingException e) {
            fail(e);
        } finally {
            if (fr != null) {
                frManager.deleteFunctionalRequirement(projectId, fr, port);
            }
            if (fo != null) {
                foManager.deleteFunctionalOutput(projectId, port, fo);
            }
        }
    }

    @Test
    public void createAndDeleteFunctionalRequirement() {
        FunctionalRequirementDAO fr = null;
        try {
            fr = frManager.createFunctionalRequirement(projectId, port);
        } catch (JSONException e) {
            fail(e);
        } finally {
            if (fr != null) {
                frManager.deleteFunctionalRequirement(projectId, fr, port);
            }
        }
    }
}

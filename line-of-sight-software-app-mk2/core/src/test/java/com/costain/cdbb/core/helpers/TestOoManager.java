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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class TestOoManager {

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    public void deleteOoFromDatabase(UUID id) { // removes OO completely from database
        ooRepository.deleteById(id);

    }

    private Map<String, String> getOirMap(String id, String text) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("oir", text);
        return map;
    }

    public OrganisationalObjectiveDAO createOrganisationalObjective(int port) throws JSONException {
        // Note this does not test the OOVersions

        System.out.println("Starting function createOrganisationalObjective");

        String testOoName = "test Objective #" + System.currentTimeMillis(); // tests rely on phrase 'Objective #'
        List<String> testOirStrings = new ArrayList<>(Arrays.asList("Test OIR #1 for " + testOoName,
                                                                    "Test OIR #2 for " + testOoName,
                                                                    "Test OIR #3 for " + testOoName));

        List<Map<String, String>> testOirs = new ArrayList<>();
        for (String oir : testOirStrings) {
            testOirs.add(getOirMap(null, oir));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", testOoName);
        map.put("oirs", testOirs);
        map.put("is_deleted", false);
        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);

        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/organisational-objectives");
        String ooResultAsJsonStr = response.getBody();

        JSONObject jsonResponse = new JSONObject(ooResultAsJsonStr);
        assertTrue(jsonResponse.has("id"), "Create oo response has no id field");
        Optional<OrganisationalObjectiveDAO> optTestOoFromDb =
            ooRepository.findById(UUID.fromString((String)jsonResponse.get("id")));
        assertTrue(optTestOoFromDb.isPresent());
        OrganisationalObjectiveDAO testOoFromDb = optTestOoFromDb.get();
        JSONArray jsonOirsArray = jsonResponse.getJSONArray("oirs");
        List<String> oirs = new ArrayList<>();
        for (int j = 0; j <  jsonOirsArray.length(); j++) {
            oirs.add((String)((JSONObject)jsonOirsArray.get(j)).get("oir"));
        }
        assertAll(
            () -> assertEquals(jsonResponse.get("id"), testOoFromDb.getId().toString()),
            () -> assertEquals(jsonResponse.get("name"), testOoFromDb.getName()),
            () -> assertEquals(jsonResponse.get("name"), testOoName),
            () -> assertTrue(oirs.containsAll(testOirStrings) && testOirStrings.containsAll(oirs))
        );
        return testOoFromDb;
    }

    public OrganisationalObjectiveDAO createAndUpdateOrganisationalObjectives(int port) throws JSONException {
        // Note this does not test the OOVersions
        OrganisationalObjectiveDAO oo = createOrganisationalObjective(port);

        return updateOrganisationalObjective(oo, port);
    }

    public OrganisationalObjectiveDAO updateOrganisationalObjective(OrganisationalObjectiveDAO oo, int port)
        throws JSONException {
        // update the name and oirs
        final String modifiedName = "Modified " + oo.getName();
        final String newOirText = "newOirText" + System.currentTimeMillis();

        // submit 2 of the original OIRs (i.e. 1 is deleted) renaming one of them
        // add a new OIR also
        List<OirDAO> origOirs = new ArrayList<>(oo.getOirDaos());
        origOirs.remove(0);
        OirDAO updatedOir = origOirs.get(0);
        origOirs.remove(0);
        origOirs.add(OirDAO.builder().id(updatedOir.getId()).oir("Updated " + updatedOir.getOir()).build());
        origOirs.add(OirDAO.builder().oir(newOirText).build());

        List<Map<String, String>> testOirs = new ArrayList<>();
        for (OirDAO oir : origOirs) {
            testOirs.add(getOirMap(oir.getId() == null ? null : oir.getId().toString(), oir.getOir()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", modifiedName);
        map.put("oirs", testOirs);
        map.put("is_deleted", oo.isDeleted());
        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);

        // wait one second to allow new OoVersion to have a later dateCreated than any previous creation
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
            payload,
            "http://localhost:" + port + "/api/organisational-objectives/" + oo.getId().toString());
        String ooResultAsJsonStr = response.getBody();
        JSONObject jsonResponse = new JSONObject(ooResultAsJsonStr);
        // read from db
        Optional<OrganisationalObjectiveDAO> optUpdatedDao = ooRepository.findById(oo.getId());
        if (!optUpdatedDao.isPresent()) {
            fail("'Modified' OO " + oo.getId() + " not found after update");
        }
        Collection<OirDAO> dbOirs = optUpdatedDao.get().getOirDaos();
        assertAll(
            () -> assertEquals(modifiedName, jsonResponse.get("name"), "Name in response incorrect"),
            () -> assertEquals(modifiedName, optUpdatedDao.get().getName(), "Name in database incorrect"),
            () -> assertEquals(origOirs.size(), jsonResponse.getJSONArray("oirs").length(),
                "Count of OIRS in not same as input"),
            () -> assertEquals(origOirs.size(), dbOirs.size(),
                "Count of OIRS in database incorrect"),
            () -> {
                JSONArray jsonArray = jsonResponse.getJSONArray("oirs");
                int countMatched = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    // check name and ids
                    JSONObject jsonOir = (JSONObject)jsonArray.get(i);
                    for (OirDAO dbOir : dbOirs) {
                        if (dbOir.getOir().equals(jsonOir.getString("oir"))) {
                            countMatched++;
                            if (dbOir.getOir().equals(newOirText)) {
                                assertTrue(dbOir.getId() != null, "dbOir.getId() should not be null");
                            } else {
                                assertEquals(dbOir.getId().toString(),  jsonOir.getString("id"),
                                    "Oir ids not equal");
                            }
                            assertEquals(dbOir.getOoId().toString(), oo.getId().toString(), "OoIds should be equal");
                        }
                    }
                }
                assertEquals(dbOirs.size(), countMatched, "Oirs matched incorrect");
            }
        );
        return oo;
    }

    public boolean deleteOoViaApi(UUID ooId, int port) { // just marks OO as deleted
        System.out.println("Starting test deleteOoViaApi");
        Optional<OrganisationalObjectiveDAO> optOo = ooId != null ? ooRepository.findById(ooId) : Optional.empty();
        if (!optOo.isPresent()) {
            System.out.println("OO " + ooId + " not found for deletion");
            fail("OO " + ooId + " not found for deletion");
            return false;
        }

        // now delete it
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/organisational-objectives/" + ooId.toString());

        // ensure all oo is marked as deleted
        Optional<OrganisationalObjectiveDAO> optDeletedDao = ooRepository.findById(ooId);
        if (!optDeletedDao.isPresent()) {
            fail("'Deleted' OO " + ooId + " not found after 'deletion'");
            return false;
        }
        assertAll(
            () -> assertTrue(optDeletedDao.get().isDeleted(), "Poos found when should have been deleted")
        );
        System.out.println("OO " + ooId + " is marked as deleted");
        return true;
    }
}

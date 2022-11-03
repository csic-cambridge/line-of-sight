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
import com.costain.cdbb.core.helpers.TestOoManager;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("no_security")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OoRestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    TestOoManager ooManager;

    @BeforeEach
    public void runBeforeAllTestMethods() {
    }


    @Test
    public void getOrganisationalObjectives() throws JSONException {
        System.out.println("Starting test getOrganisationalObjectives");
        // fetch all from database
        // fetch all from api
        Iterable<OrganisationalObjectiveDAO> ooDaos = ooRepository.findByIsDeleted(false);

        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/organisational-objectives");
        JSONArray jsonResponse = new JSONArray(response.getBody());
        assertAll(
            () -> assertEquals(jsonResponse.length(), ((Collection<?>) ooDaos).size())
        );

        ooDaos.forEach(dao -> {
            // find the oo in response data
            JSONObject jsonSingleResponse = null;
            boolean matched = false;
            for (int i = 0; i < jsonResponse.length(); i++) {
                try {
                    jsonSingleResponse = jsonResponse.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail("JSONException while reading response array");
                }
                try {
                    if (jsonSingleResponse != null
                        && jsonSingleResponse.getString("id").equals(dao.getId().toString())) {
                        matched = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    fail("JSONException while reading id from response oo");
                }
            }
            assertTrue(matched, "OO with id = " + dao.getId() + " not returned from fetch all OOs");
            try {
                    JSONArray jsonOirsArray = jsonSingleResponse.getJSONArray("oirs");
                    List<String> responseOirs = new ArrayList<>();
                    for (int i = 0; i <  jsonOirsArray.length(); i++) {
                        JSONObject oirJson = (JSONObject)jsonOirsArray.get(i);
                        responseOirs.add(oirJson.getString("oir"));
                    }
                    List<String> dbOirs = new ArrayList<>();
                    for (OirDAO oirDao : dao.getOirDaos()) {
                        dbOirs.add(oirDao.getOir());
                    }
                JSONObject finalJsonSingleResponse = jsonSingleResponse;
                assertAll(
                        () -> assertTrue(finalJsonSingleResponse.get("id").equals(dao.getId().toString())),
                        () -> assertTrue(finalJsonSingleResponse.get("name").equals(dao.getName())),
                        () -> assertTrue(responseOirs.containsAll(dbOirs) && dbOirs.containsAll(responseOirs))
                    );
                } catch (JSONException e) {
                    fail(e);
                }
            }
        );
    }


    @Test
    public void addAndDeleteOrganisationalObjectives() throws JSONException {
        // Need to make sure all projects have a poo added for the new OO
        OrganisationalObjectiveDAO oo = ooManager.createOrganisationalObjective(port);
        List<ProjectDAO> allProjects = (List<ProjectDAO>)projectRepository.findAll();
        for (ProjectDAO projectDao : allProjects) {
            List<ProjectOrganisationalObjectiveDAO> pooDaos =
                pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectDao.getId());
            boolean pooFoundForNewOo = false;
            for (ProjectOrganisationalObjectiveDAO pooDao : pooDaos) {
                pooFoundForNewOo = pooDao.getOoVersion().getOo().getId().equals(oo.getId());
                if (pooFoundForNewOo) {
                    break;
                }
            }
            assertTrue(pooFoundForNewOo, "Poo not found for project " + projectDao.getId() + ", oo=" + oo.getId());
        }

        assertTrue(ooManager.deleteOoViaApi(oo.getId(), port));
        ooManager.deleteOoFromDatabase(oo.getId());
        System.out.println("Oo " + oo.getId() + " is created and deleted");
    }

    @Test
    public void updateOrganisationalObjectives() throws JSONException {
        OrganisationalObjectiveDAO oo = ooManager.createAndUpdateOrganisationalObjectives(port);
        assertTrue(ooManager.deleteOoViaApi(oo.getId(), port));
        ooManager.deleteOoFromDatabase(oo.getId());
        System.out.println("Oo " + oo.getId() + " is created and deleted");
    }
}

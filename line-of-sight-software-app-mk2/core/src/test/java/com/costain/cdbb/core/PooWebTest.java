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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestFunctionalOutputManager;
import com.costain.cdbb.core.helpers.TestFunctionalRequirementManager;
import com.costain.cdbb.core.helpers.TestOoManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.OirRepository;
import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


/*
Project Organisational Objectives Api design is more complex than most of the Apis
Fetch - gets all POOs for the project including the last updated version and all versions since
      - if the OO is deleted and the POO has no frs the it will not be sent
Update - Updates the POO latest version to the version id sent

 */
@ActiveProfiles("no_security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PooWebTest {
    @LocalServerPort
    private int port;
    private static UUID projectId;
    private static FunctionalOutputDataDictionaryDAO foDdDao;
    private Set<String> sourceFos;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    @Autowired
    OoVersionRepository oovRepository;

    @Autowired
    OirRepository oirRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestOoManager ooManager;

    @Autowired
    TestFunctionalRequirementManager frManager;

    @Autowired
    TestFunctionalOutputManager foManager;


    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            projectId = projectManager.create("Project Organisational Objective Test", port);
            foDdDao = foManager.createFoDdWithEntry(projectId, port);
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
            foManager.deleteFunctionalOutputDictionary(foDdDao.getId());
            projectManager.delete(projectId, port);
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
    }

    private List<OoVersionDAO> getCurrentAndFutureOoVersionsForOo(
        ProjectOrganisationalObjectiveDAO poo, OrganisationalObjectiveDAO oo) {
        // get current and future oo_versions for this poo but do not use query used in api
        // - get them all and remove ones already discarded
        List<OoVersionDAO> ooVersionsFromDb = oovRepository.findByOoIdOrderByDateCreated(oo.getId());
        ProjectOrganisationalObjectiveDAO finalPooFromDb = poo;
        ooVersionsFromDb.removeIf(x -> x.getDateCreated().before(finalPooFromDb.getOoVersion().getDateCreated()));
        return ooVersionsFromDb;
    }

    private List<ProjectOrganisationalObjectiveDAO> checkPooResponseAgainstDatabase(String pooResultAsJsonStr)
        throws JSONException {
        JSONArray pooJsonArray = new JSONArray(pooResultAsJsonStr);
        List<ProjectOrganisationalObjectiveDAO> poosFromDb =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
        // need to remove poos deleted and with no Frs
        poosFromDb.removeIf(poo -> (poo.getFrs() == null || poo.getFrs().size() == 0)
            && (poo.getOoVersion().getOo().isDeleted()));
        assertEquals(pooJsonArray.length(), poosFromDb.size(), "Unexpected number of Poos fetched");
        for (int i = 0; i < pooJsonArray.length(); i++) {
            JSONObject pooJsonObject = (JSONObject) pooJsonArray.get(i);
            System.out.println(pooJsonObject);
            ProjectOrganisationalObjectiveDAO pooFromDb = null;
            for (ProjectOrganisationalObjectiveDAO dao : poosFromDb) {
                if (dao.getId().toString().equals(pooJsonObject.getString("id"))) {
                    pooFromDb = dao;
                    break;
                }
            }
            assertTrue(pooFromDb != null);
            poosFromDb.remove(pooFromDb); // remove so not matched again
            JSONArray ooVersionJsonArray = pooJsonObject.getJSONArray("oo_versions");
            // get all current and future oo_versions for this poo but do not use query used in api
            // - get them all and remove ones already discarded
            List<OoVersionDAO> ooVersionsFromDb =
                getCurrentAndFutureOoVersionsForOo(pooFromDb, pooFromDb.getOoVersion().getOo());

            ProjectOrganisationalObjectiveDAO finalPooFromDb = pooFromDb;
            assertAll(
                () -> assertEquals(projectId.toString(), pooJsonObject.getString("project_id")),
                () -> assertEquals(pooJsonObject.getBoolean("oo_is_deleted"),
                                finalPooFromDb.getOoVersion().getOo().isDeleted(), "IsDeleted incorrect"),
                () -> assertEquals(ooVersionJsonArray.length(), ooVersionsFromDb.size()),
                () -> assertEquals(((JSONObject)ooVersionJsonArray.get(0)).getString("name"),
                    pooJsonObject.getString("name"))
            );
            long lastDateCreated = 0L;
            for (int j = 0; j < ooVersionJsonArray.length(); j++) {
                JSONObject ooVersionJsonObject = ooVersionJsonArray.getJSONObject(j);
                long finalLastDateCreated = lastDateCreated;

                assertAll(
                    () -> assertTrue(ooVersionJsonObject.getLong("date_created") > finalLastDateCreated,
                        "Expected date_created " + ooVersionJsonObject.getLong("date_created")
                        + " is unexpectedly < " + finalLastDateCreated),
                    () -> assertTrue(ooVersionJsonObject.getString("name").contains("Objective #"),
                        "Objective expected to contain 'Objective #' but was "
                            + ooVersionJsonObject.getString("name")),
                    () -> assertTrue(ooVersionJsonObject.getString("oo_id") != null,
                        "oo_id should not be null"),
                    () -> assertTrue(ooVersionJsonObject.getString("id") != null,
                        "id should not be null")
                );
                lastDateCreated = ooVersionJsonObject.getLong("date_created");
            }
            // check OIRs
            // active oirs...
            JSONArray oirsJsonArray = pooJsonObject.getJSONArray("oirs");
            List<String> oirsFromResponse = new ArrayList<>();
            for (int k = 0; k < oirsJsonArray.length(); k++) {
                oirsFromResponse.add((((JSONObject)oirsJsonArray.get(k)).getString("oir")));
            }
            // deleted oirs ..
            JSONArray deletedOirsJsonArray = pooJsonObject.getJSONArray("deleted_oirs");
            List<String> deletedOirsFromResponse = new ArrayList<>();
            for (int k = 0; k < deletedOirsJsonArray.length(); k++) {
                deletedOirsFromResponse.add((((JSONObject)deletedOirsJsonArray.get(k)).getString("oir")));
            }
            Collection<OirDAO> oirsFromDb = pooFromDb.getOoVersion().getOo().getOirDaos();
            List<OirDAO> deletedOirDaos = oirRepository.findDeletedOirsForProjectAndOo(
                pooFromDb.getProjectId().toString(),
                pooFromDb.getOoVersion().getOo().getId().toString()
            );

            List<String> dbOirStrings = new ArrayList<>();
            List<String> dbDeletedOirStrings = new ArrayList<>();
            oirsFromDb.forEach(oirDao -> dbOirStrings.add(oirDao.getOir()));
            // remove deleted ones
            deletedOirDaos.forEach(deletedDao -> {
                dbOirStrings.remove(deletedDao.getOir());
                dbDeletedOirStrings.add(deletedDao.getOir());
            });
            assertEquals(oirsFromResponse.size(), dbOirStrings.size(), "Unexpected number of OIRs");
            assertTrue(dbOirStrings.containsAll(oirsFromResponse)
                && oirsFromResponse.containsAll(dbOirStrings));

            assertTrue(dbDeletedOirStrings.containsAll(deletedOirsFromResponse)
                && deletedOirsFromResponse.containsAll(dbDeletedOirStrings));

            // check frs
            JSONArray frsJsonArray = pooJsonObject.getJSONArray("frs");
            List<String> frsFromResponse = new ArrayList<>();
            for (int k = 0; k < frsJsonArray.length(); k++) {
                frsFromResponse.add((String)frsJsonArray.get(k));
            }
            // copy frs ids from db to separate list
            ArrayList<String> frsFromDb = new ArrayList<>();
            for (FunctionalRequirementDAO fr : pooFromDb.getFrs()) {
                frsFromDb.add(fr.getId().toString());
            }
            assertEquals(frsFromResponse.size(), pooFromDb.getFrs().size(),  "Unexpected number of FRs");
            assertTrue(frsFromDb.containsAll(frsFromResponse) && frsFromResponse.containsAll(frsFromDb));
        }
        return pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
    }

    private List<ProjectOrganisationalObjectiveDAO> fetchAndCheckCurrentPoosForProject() throws JSONException {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project-organisational-objectives/" + projectId);
        return checkPooResponseAgainstDatabase(response.getBody());
    }

    private List<ProjectOrganisationalObjectiveDAO> performAndCheckUpdateToPoo(
        ProjectOrganisationalObjectiveDAO poo,
        OoVersionDAO updatedOoVersion,
        Set<String> frs) throws JSONException {
        // Check this update works by comparing before and after oo_versions in the database,
        // then let the poo fetch confirm
        List<OoVersionDAO> origOoVersionsFromDb =
            getCurrentAndFutureOoVersionsForOo(poo, poo.getOoVersion().getOo());
        Map<String, Object> map = new HashMap<>();
        map.put("id", poo.getId());
        if (updatedOoVersion != null) {
            map.put("oo_version_id", updatedOoVersion.getId());
        }
        if (frs != null) {
            map.put("frs", frs);
        }
        // construct current oir ids
        List<String> oirIds = new ArrayList<>();
        poo.getOoVersion().getOo().getOirDaos().forEach(oirDao -> oirIds.add(oirDao.getId().toString()));
        oirRepository.findDeletedOirsForProjectAndOo(
            projectId.toString(), poo.getOoVersion().getOo().getId().toString()).forEach(
                deletedOirDao -> oirIds.remove(deletedOirDao.getId()) // may have already previously been removed
        );
        map.put("oir_ids", oirIds);

        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);
        ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
            payload,
            "http://localhost:" + port + "/api/project-organisational-objectives/" + projectId + "/" + poo.getId());
        Optional<ProjectOrganisationalObjectiveDAO> optUpdatedPoo = pooRepository.findById(poo.getId());
        assertTrue(((Optional<?>) optUpdatedPoo).isPresent(), "Cannot find update poo");

        if (updatedOoVersion != null) {
            List<OoVersionDAO> updatedOoVersionsFromDb =
                getCurrentAndFutureOoVersionsForOo(optUpdatedPoo.get(), optUpdatedPoo.get().getOoVersion().getOo());
            assertTrue(updatedOoVersionsFromDb.size() < origOoVersionsFromDb.size(),
                "Expected fewer Oo_versions after update");
            assertEquals(updatedOoVersionsFromDb.get(0).getId(), updatedOoVersion.getId(),
                "Current oo_version not updated to requested one");
        }
        if (frs != null) {
            assertEquals(optUpdatedPoo.get().getFrs().size(), frs.size(), "Unexpected FR size");
        }
        return fetchAndCheckCurrentPoosForProject();
    }

    @Test
    public void fetchAndUpdatePoosForProject() {
        // This is the only test for Poos and involves the fetch and updating of poos

        // need to create multiple oo_versions and then update the poo to each one in turn
        // also need to repeat this by deleting the oo and check the poo is delivered until the last fr is removed

        /* Process steps
            1) Create new project and check there are the correct values with only 1 oo_version for each poo
            2) Update an OO twice and check that 2 then 3 ooversions come back (this counting is done by interrogating
            the db so need to be sure that process works reliably!)
            3) Accept the first change and check that change updates the oo_versions accordingly
               and check the poo result matches
            4) Accept the second change and check that change updates the oo_versions accordingly
               and check the poo result matches
            5) Create a new OO and update it twice and check the poo matches
            6) Delete the OIRs of a POO 1 by 1 and check retrieved as deleted oirs only
            7) Create 2 frs for the poo
            8) Mark the OO 'deleted'
               and check the poo is still present i.e. the poo
            9) remove all frs
            10) check the poo is no longer retrieved
         */

        try {
            //
            // STEP 1 - Create new project and check there are the correct values with only 1 oo_version for each poo
            fetchAndCheckCurrentPoosForProject();

            // STEP 2 Update an OO twice and check that 2 then 3 ooversions come back
            // (this counting is done by interrogating the db so need to be sure that process works reliably!)
            OrganisationalObjectiveDAO updatedOo = ooManager.createOrganisationalObjective(port);
            ooManager.updateOrganisationalObjective(updatedOo, port);
            fetchAndCheckCurrentPoosForProject();
            updatedOo = ooRepository.findById(updatedOo.getId()).get();
            ooManager.updateOrganisationalObjective(updatedOo, port);
            List<ProjectOrganisationalObjectiveDAO> poosFromDb = fetchAndCheckCurrentPoosForProject();

            // STEP 3 Accept the first oo change and check that change updates the oo_versions accordingly
            // and check the poo result matches
            for (ProjectOrganisationalObjectiveDAO pooDao : poosFromDb) {
                if (pooDao.getOoVersion().getOo().getId().toString().equals(updatedOo.getId().toString())) {
                    List<OoVersionDAO> currentAndFutureOoVersionsForOo = getCurrentAndFutureOoVersionsForOo(
                        pooDao,updatedOo);
                    poosFromDb = performAndCheckUpdateToPoo(pooDao, currentAndFutureOoVersionsForOo.get(1), null);
                    break;
                }
            }
            // STEP 4 Accept the second oo change and check that change updates the oo_versions accordingly
            // and check the poo result matches
            for (ProjectOrganisationalObjectiveDAO pooDao : poosFromDb) {
                if (pooDao.getOoVersion().getOo().getId().toString().equals(updatedOo.getId().toString())) {
                    List<OoVersionDAO> currentAndFutureOoVersionsForOo = getCurrentAndFutureOoVersionsForOo(
                        pooDao,updatedOo);
                    poosFromDb = performAndCheckUpdateToPoo(pooDao, currentAndFutureOoVersionsForOo.get(1), null);
                    break;
                }
            }
            ooManager.deleteOoFromDatabase(updatedOo.getId());

            // STEP 5 Create a new OO and update it twice and check the poo matches
            OrganisationalObjectiveDAO newOo = ooManager.createOrganisationalObjective(port);
            ooManager.updateOrganisationalObjective(newOo, port);
            ooManager.updateOrganisationalObjective(newOo, port);
            poosFromDb = fetchAndCheckCurrentPoosForProject();

            // STEP 6 Delete the OIRs of a POO 1 by 1 and check not retrieved
            // Find POO with at least 3 oirs
            final ProjectOrganisationalObjectiveDAO[] pooForOirDeletion = {null};
            poosFromDb.forEach(poo -> {
                if (poo.getOoVersion().getOo().getOirDaos().size() >= 3) {
                    pooForOirDeletion[0] = poo;
                }
            });
            assertTrue(pooForOirDeletion[0] != null);
            int deleteCount = 0;
            List<OirDAO> origOirDaos = new ArrayList<>(pooForOirDeletion[0].getOoVersion().getOo().getOirDaos());
            for (OirDAO deletedOirDao : origOirDaos) {
                deleteCount++;
                assertTrue(pooForOirDeletion[0].getOoVersion().getOo().getOirDaos().remove(deletedOirDao));
                assertEquals(origOirDaos.size() - deleteCount,
                    pooForOirDeletion[0].getOoVersion().getOo().getOirDaos().size());
                poosFromDb = performAndCheckUpdateToPoo(pooForOirDeletion[0], null, null);
                // check deleted OIR is in deleted list for project
                List<OirDAO> deletedOirDaosFromDb = oirRepository.findDeletedOirsForProjectAndOo(
                    projectId.toString(), pooForOirDeletion[0].getOoVersion().getOo().getId().toString());
                assertEquals(deleteCount, deletedOirDaosFromDb.size());
                List<String> deletedOirIdsFromDb = deletedOirDaosFromDb.stream().map(
                    deletedOirDaoFromDb -> deletedOirDaoFromDb.getId().toString()).toList();
                assertTrue(deletedOirIdsFromDb.contains(deletedOirDao.getId().toString()));
            }

            // STEP 7 Create 2 frs for the poo
            UUID pooId = null; // the poo which has the modified oo
            FunctionalRequirementDAO fr1 = frManager.createFunctionalRequirement(projectId, port);
            FunctionalRequirementDAO fr2 = frManager.createFunctionalRequirement(projectId, port);
            Set<String> frs = new HashSet<>(Arrays.asList(fr1.getId().toString(), fr2.getId().toString()));
            for (ProjectOrganisationalObjectiveDAO pooDao : poosFromDb) {
                if (pooDao.getOoVersion().getOo().getId().toString().equals(newOo.getId().toString())) {
                    poosFromDb = performAndCheckUpdateToPoo(pooDao, null, frs);
                    pooId = pooDao.getId();
                    break;
                }
            }
            assertTrue(pooId != null, "Poo for testing not found");

            // STEP 8 Mark the OO 'deleted' and check the poo is still present
            newOo.setIsDeleted(true);
            ooManager.updateOrganisationalObjective(newOo, port);
            newOo = ooRepository.findById(newOo.getId()).get();
            assertTrue(newOo.isDeleted(), "OO should have been marked as deleted");
            // check we still have same number of poos in db
            int previousPooCount = poosFromDb.size();
            poosFromDb = pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
            assertEquals(poosFromDb.size(), previousPooCount, "Unexpected poo count after deleting OO");
            fetchAndCheckCurrentPoosForProject();

            // STEP 9  remove all frs from the poo
            ProjectOrganisationalObjectiveDAO pooFromDb = pooRepository.findById(pooId).get();
            poosFromDb = performAndCheckUpdateToPoo(pooFromDb, null, new HashSet<String>());

            // STEP 10  check the poo is no longer retrieved from api
            ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/project-organisational-objectives/" + projectId);
            JSONArray pooJsonArray = new JSONArray(response.getBody());
            for (int i = 0; i < pooJsonArray.length(); i++) {
                JSONObject pooJsonObject = pooJsonArray.getJSONObject(i);
                assertNotEquals(pooJsonObject.getString("id"), pooId.toString(),
                    "Should not have found poo with id " + pooId);
            }

            assertTrue(ooManager.deleteOoViaApi(newOo.getId(), port));
            ooManager.deleteOoFromDatabase(newOo.getId());
            System.out.println("Oo1 " + newOo.getId() + " is created and deleted");
        } catch (Exception e) {
            fail(e);
        }
    }
}

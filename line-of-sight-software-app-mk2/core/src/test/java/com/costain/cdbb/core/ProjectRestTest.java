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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("no_security")

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProjectRestTest {



    @LocalServerPort
    private int port;

    private JSONObject projectJsonObject;

    private static final String SAMPLE_PROJECT_ID = "387dac90-e188-11ec-8fea-0242ac120002";
    private static final String SAMPLE_PROJECT_NAME = "Sample Project";

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    AssetRepository assetRepository;

    @BeforeEach
    public void runBeforeAllTestMethods() throws JSONException {
        projectJsonObject = new JSONObject();
        projectJsonObject.put("name","Project " + System.currentTimeMillis());
        projectJsonObject.put("fo_dd_id","97ee7a74-e8c7-11ec-8fea-0242ac120002");
        projectJsonObject.put("asset_dd_id", "e1970a24-e8c7-11ec-8fea-0242ac120002");
    }

    @Test
    public void getProjectList() {
        System.out.println("Starting test getProjectList");
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project");
        assertAll(
            () -> assertTrue(response.getBody().contains("Sample Project"),
                "Get project list does not include expected project")
        );
    }

    private UUID createProjectFn() throws Exception {
        System.out.println("Starting test createProjectFn");
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            projectJsonObject.toString(),
            "http://localhost:" + port + "/api/project");


        String personResultAsJsonStr = response.getBody();
        JSONObject jsonObject = new JSONObject(personResultAsJsonStr);
        assertTrue(jsonObject.has("id"), "Create project response has no id field");
        UUID projectId = UUID.fromString((String) jsonObject.get("id"));
        Set<OrganisationalObjectiveDAO> ooDaos = ooRepository.findByIsDeleted(false);
        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
        assertAll(
            () -> assertTrue((ooDaos.size() == pooDaos.size()) && (ooDaos.size() > 0),
                "Expected " + ooDaos.size() + " poos but found " + pooDaos.size()),
            () -> assertTrue(jsonObject.has("name"), "Create project response has no name field")
        );
        System.out.println("Project " + projectId + " is created");
        return projectId;
    }

    private void deleteProjectFn(UUID projectId) {
        //Delete the created project
        System.out.println("Starting test deleteProjectFn");
        Optional<ProjectDAO> optProject = projectId != null ? projectRepository.findById(projectId) : Optional.empty();
        if (optProject.isEmpty()) {
            System.out.println("Project " + projectId + " not found for deletion");
            return;
        }
        //ProjectDAO project = optProject.get();

        // Using api,  add some functional requirements, [ to do : fos, firs, assets and airs]
        //TODO
        for (int i = 1; i < 4; i++) {

        }

        // now delete it
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/project/pid/" + projectId);

        // ensure all data for project is deleted

        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
        Set<FunctionalRequirementDAO> frDaos = frRepository.findByProjectIdOrderByNameAsc(projectId);
        assertAll(
            () -> assertTrue(pooDaos.isEmpty(), "Poos found when should have been deleted"),
            () -> assertTrue(frDaos.isEmpty(), "Functional requirements found when should have been deleted")
        );
        System.out.println("Project " + projectId + " is deleted");
    }

    @Test
    public void createAndDeleteProject() throws Exception {
        UUID createdProjectId =  createProjectFn();
        deleteProjectFn(createdProjectId);
    }

    private boolean findPooWithMatchingOoVersionFromPooList(
        // find poo with matching ooversion - check its frs have different ids but same name
        ProjectDAO matchProject, ProjectOrganisationalObjectiveDAO pooToMatchAgainstSource,
        Set<ProjectOrganisationalObjectiveDAO> sourcePoos) {
        List<ProjectOrganisationalObjectiveDAO> matchedPooList =  sourcePoos.stream().filter(
            sourcePoo -> {
                System.out.println("Comparing project ids - " + sourcePoo.getProjectId() + " - "
                    +  matchProject.getId());
                System.out.println("Comparing ooversion ids - " + pooToMatchAgainstSource.getOoVersion().getId()
                    + " - " + sourcePoo.getOoVersion().getId());
                return sourcePoo.getProjectId().equals(matchProject.getId())
                    && pooToMatchAgainstSource.getOoVersion().getId().equals(sourcePoo.getOoVersion().getId());
            }).toList();
        if (matchedPooList.size() == 1) {
            // match expected - now check the frs
            return matchFrsNamesNotIds(matchProject, pooToMatchAgainstSource.getFrs(), matchedPooList.get(0).getFrs());
        }
        System.out.println("Failed to match Poo");
        return false;
    }

    private boolean matchFrsNamesNotIds(
        ProjectDAO matchProject, Set<FunctionalRequirementDAO> frsToMatch, Set<FunctionalRequirementDAO> sourceFrs) {
        int result =  (int) frsToMatch.stream().filter(
            frToMatch ->
                sourceFrs.stream().filter(
                    sourceFr -> {
                        System.out.println("Comparing frs - "
                            + sourceFr.getProjectId() + " - " + matchProject.getId());
                        System.out.println("Comparing fr ids - " + frToMatch.getId() + " - " + sourceFr.getId());
                        System.out.println("Comparing fr names - "
                            + frToMatch.getName() + " - " + sourceFr.getName());
                        return sourceFr.getProjectId().equals(matchProject.getId())
                            && !frToMatch.getId().equals(sourceFr.getId()) // ids must NOT match
                            && frToMatch.getName().equals(sourceFr.getName());
                    }).count() == 1
            ).count();
        System.out.println("Result = " + result + " to match " + frsToMatch.size());
        return result == frsToMatch.size();
    }

    @Test
    public void copySampleProject() throws Exception {
        System.out.println("Starting test copySampleProject");

        Optional<ProjectDAO> optProject = projectRepository.findById(UUID.fromString(SAMPLE_PROJECT_ID));
        assertTrue(optProject.isPresent(), "Source project " + SAMPLE_PROJECT_ID + " not found for copying");

        String originalProjectName = (String)projectJsonObject.get("name");
        String startOfCopiedProjectName = "New Copy Of " + originalProjectName;
        JSONObject copyProjectJsonObject = new JSONObject();
        copyProjectJsonObject.put("name", startOfCopiedProjectName);
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            copyProjectJsonObject.toString(),
            "http://localhost:" + port + "/api/project/pid/" + SAMPLE_PROJECT_ID);
        String personResultAsJsonStr = response.getBody();

        JSONObject jsonObject = new JSONObject(personResultAsJsonStr);
        assertTrue(jsonObject.has("id"), "Create project response has no id field");
        UUID copiedProjectId = UUID.fromString((String) jsonObject.get("id"));

        String newProjectName = (String) jsonObject.get("name");
        Set<OrganisationalObjectiveDAO> ooDaos =
            StreamSupport.stream(ooRepository.findAll().spliterator(), false)
            .collect(Collectors.toSet());;
        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(copiedProjectId);

        assertAll(
            () -> assertEquals(ooDaos.size(), pooDaos.size(),
                "Expected " + ooDaos.size() + " poos but found " + pooDaos.size()),
            () -> assertTrue(jsonObject.has("name"), "Create project response has no name field"),

            () -> assertNotEquals(SAMPLE_PROJECT_ID, copiedProjectId.toString()),
            () -> assertEquals("New Copy Of " + originalProjectName, newProjectName)
        );
        System.out.println("Project " + originalProjectName + " with Id " + SAMPLE_PROJECT_ID
            + " was copied to project " + newProjectName + " with Id " + copiedProjectId);

        // test copied project by fetching properties from database
        ProjectDAO copiedProject = projectRepository.findById(copiedProjectId).get();
        ProjectDAO sourceProject = optProject.get();
        System.out.println("Source project = " + sourceProject);
        System.out.println("Copied project = " + copiedProject);
        assertAll(
            () -> assertTrue(copiedProject.getName().startsWith(startOfCopiedProjectName)),
            () -> assertEquals(sourceProject.getAssetDataDictionary(), copiedProject.getAssetDataDictionary(),
                "Asset Data Dictionary incorrect"),
            () -> assertEquals(sourceProject.getFoDataDictionary(), copiedProject.getFoDataDictionary(),
                "FO Data Dictionary incorrect"),
            () -> assertEquals(sourceProject.getProjectOrganisationalObjectiveDaos().size(),
                copiedProject.getProjectOrganisationalObjectiveDaos().size(),
                "Different number of POOs"),
            () -> assertEquals(sourceProject.getFunctionRequirementDaos().size(),
                    copiedProject.getFunctionRequirementDaos().size(),
                "Different number of FRs")
        );
        // check Poos using ooVersions and frs under each (already tested for same number)
        int matchingPoos = (int) sourceProject.getProjectOrganisationalObjectiveDaos().stream().filter(sourceDao ->
            findPooWithMatchingOoVersionFromPooList(
                copiedProject, sourceDao, copiedProject.getProjectOrganisationalObjectiveDaos())
            ).count();
        assertEquals(matchingPoos, sourceProject.getProjectOrganisationalObjectiveDaos().size(),
            "Unexpected number of copied Poos");

        // test total Frs (some may not be linked)
        Set<FunctionalRequirementDAO> sourceFrs = frRepository.findByProjectIdOrderByNameAsc(sourceProject.getId());
        Set<FunctionalRequirementDAO> copiedFrs = frRepository.findByProjectIdOrderByNameAsc(copiedProject.getId());
        assertEquals(sourceFrs.size(), copiedFrs.size(), "Total number of Frs does not match");

        // Data Dictionaries - Functional Outputs
        Set<FunctionalOutputDAO> sourceFos = foRepository.findByProjectId(sourceProject.getId());
        Set<FunctionalOutputDAO> copiedFos = foRepository.findByProjectId(copiedProject.getId());
        assertEquals(sourceFos.size(), copiedFos.size(), "Total number of Fos does not match");

        // check firs counts
        Map<String, FunctionalOutputDAO> sourceFosMap = new HashMap<>();
        sourceFos.stream().forEach(foDao -> sourceFosMap.put(foDao.getDataDictionaryEntry().getId(), foDao));
        Map<String, FunctionalOutputDAO> copiedFosMap = new HashMap<>();
        copiedFos.stream().forEach(foDao -> copiedFosMap.put(foDao.getDataDictionaryEntry().getId(), foDao));
        sourceFos.stream().forEach(foDao ->
            assertTrue(sourceFosMap.get(foDao.getDataDictionaryEntry().getId()).getFirs()
                .containsAll(copiedFosMap.get(foDao.getDataDictionaryEntry().getId()).getFirs())
                && copiedFosMap.get(foDao.getDataDictionaryEntry().getId()).getFirs()
                .containsAll(sourceFosMap.get(foDao.getDataDictionaryEntry().getId()).getFirs())));

        // Data Dictionaries - Assets
        Set<AssetDAO> sourceAssets = assetRepository.findByProjectId(sourceProject.getId());
        Set<AssetDAO> copiedAssets = assetRepository.findByProjectId(copiedProject.getId());
        assertEquals(sourceAssets.size(), copiedAssets.size(), "Total number of Assets does not match");

        // check airs counts
        Map<String, AssetDAO> sourceAssetsMap = new HashMap<>();
        sourceAssets.stream().forEach(assetDao ->
            sourceAssetsMap.put(assetDao.getDataDictionaryEntry().getId(), assetDao));
        Map<String, AssetDAO> copiedAssetsMap = new HashMap<>();
        copiedAssets.stream().forEach(assetDao ->
            copiedAssetsMap.put(assetDao.getDataDictionaryEntry().getId(), assetDao));
        sourceAssets.stream().forEach(assetDao ->
            assertTrue(sourceAssetsMap.get(assetDao.getDataDictionaryEntry().getId()).getAirs()
                .containsAll(copiedAssetsMap.get(assetDao.getDataDictionaryEntry().getId()).getAirs())
                && copiedAssetsMap.get(assetDao.getDataDictionaryEntry().getId()).getAirs()
                .containsAll(sourceAssetsMap.get(assetDao.getDataDictionaryEntry().getId()).getAirs())));

        deleteProjectFn(copiedProjectId);
    }

    @Test
    public void fetchSampleProject() {
        // fetch the sample project as the front-end would do
        // 1) fetch project
        /*final ResponseEntity<String> projectResponse = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project/pid/" + SAMPLE_PROJECT_ID);
        try {
            final JSONObject jsonProjectResponse = new JSONObject(projectResponse.getBody());
            assertAll(
                () -> assertEquals(jsonProjectResponse.get("id"), SAMPLE_PROJECT_ID,
                    "Get sample project does not have correct id"),
                () -> assertEquals(jsonProjectResponse.get("name"), SAMPLE_PROJECT_NAME,
                    "Get sample project does not have correct name")
            );
            System.out.println("Project header fetched");*/
        // fetch poos
        final ResponseEntity<String> pooResponse =  apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/project-organisational-objectives/pid/" + SAMPLE_PROJECT_ID);
        try {
            final JSONArray jsonPooResponseRaw = new JSONArray(pooResponse.getBody());
            List<JSONObject> jsonPooResponse = IntStream
                .range(0, jsonPooResponseRaw.length())
                .mapToObj(i -> {
                    try {
                        return jsonPooResponseRaw.getJSONObject(i);
                    } catch (JSONException e) {
                        fail(e.toString());
                        return null;
                    }
                }).toList();

            assertAll(
                //() -> assertEquals(3, jsonPooResponseRaw.length(),
                //    "Expected 3 Poos but got " + jsonPooResponseRaw.length()),
                () -> jsonPooResponse
                    .forEach(jsonObject -> {
                        try {
                            assertFalse((boolean) jsonObject.get("oo_is_deleted"), "The json object is deleted");
                        } catch (JSONException e) {
                            fail(e);
                        }
                    })
            );
            System.out.println("Poos fetched");
            //TODO to be completed
        }  catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    public void renameProject() {
        System.out.println("Starting test renameProject");
        final String NewProjectName = "This project has been renamed";

        UUID createdProjectId = null;
        try {
            createdProjectId = createProjectFn();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }

        try {
            JSONObject renameProjectJsonObject = new JSONObject();
            renameProjectJsonObject.put("name", NewProjectName);
            apiManager.doSuccessfulPutApiRequest(
                renameProjectJsonObject.toString(),
                "http://localhost:" + port + "/api/project/pid/" + createdProjectId);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
        Optional<ProjectDAO> optProject = projectRepository.findById(createdProjectId);
        assertTrue(optProject.isPresent(), "Created project "
            + createdProjectId + " not found for checking rename");
        assertAll(
            () -> assertEquals(optProject.get().getName(), NewProjectName, "Project rename failed")
        );
        try {
            deleteProjectFn(createdProjectId);
        } catch (Exception e) {
            fail(e);
        }
    }
}

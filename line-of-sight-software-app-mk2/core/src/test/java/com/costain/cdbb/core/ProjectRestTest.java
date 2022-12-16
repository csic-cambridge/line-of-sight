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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestDataDictionaryManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.helpers.CompressionHelper;
import com.costain.cdbb.model.helpers.ProjectImportExportHelper;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
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

    private int projectsWithSampleProjectFoDictionary;
    private int projectsWithSampleProjectAssetDictionary;

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    private TestProjectManager projectManager;

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

    @Autowired
    ProjectImportExportHelper projectImportExportHelper;

    @Autowired
    TestDataDictionaryManager dataDictionaryManager;

    @Autowired
    AssetDataDictionaryRepository assetDdRepository;

    @Autowired
    FunctionalOutputDataDictionaryRepository foDdRepository;

    @Autowired
    CompressionHelper compressionHelper;

    @BeforeEach
    public void runBeforeAllTestMethods() throws JSONException {
        setInitialProjectJsonObject();
    }

    private void setInitialProjectJsonObject() throws JSONException {
        projectJsonObject = new JSONObject();
        projectJsonObject.put("name","Project " + System.currentTimeMillis());
        projectJsonObject.put("fo_dd_id",TestApiManager.sampleFoDdId.toString());
        projectJsonObject.put("asset_dd_id", TestApiManager.sampleAssetDdId.toString());
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
        createAndDeleteProjectWithImportFrsAndAirs(null, null);
        createAndDeleteProjectWithImportFrsAndAirs(TestApiManager.sampleProjectId, null);
        createAndDeleteProjectWithImportFrsAndAirs(null, TestApiManager.sampleProjectId);
        createAndDeleteProjectWithImportFrsAndAirs(TestApiManager.sampleProjectId, TestApiManager.sampleProjectId);
        UUID copiedProjectId = createCopyOfSampleProject();
        createAndDeleteProjectWithImportFrsAndAirs(copiedProjectId, TestApiManager.sampleProjectId);
        deleteProjectFn(copiedProjectId);
    }

    private void createAndDeleteProjectWithImportFrsAndAirs(UUID importFirsProjectId, UUID importAirsProjectId)
        throws Exception {
        setInitialProjectJsonObject();
        Set<FunctionalOutputDAO> importableFos = null;
        Set<AssetDAO> importableAssets = null;
        UUID createdProjectId = createProjectFn();
        deleteProjectFn(createdProjectId);
        // now create and import the fos and assets from sample project
        if (null != importFirsProjectId) {
            projectJsonObject.put("import_firs_project_id", importFirsProjectId.toString());
            importableFos = foRepository.findByProjectId(importFirsProjectId);
        }
        if (null != importAirsProjectId) {
            projectJsonObject.put("import_airs_project_id", importAirsProjectId.toString());
            importableAssets = assetRepository.findByProjectId(importAirsProjectId);
        }
        createdProjectId = createProjectFn();
        // check fos/firs/assets and airs copied across
        Set<FunctionalOutputDAO> addedFos = foRepository.findByProjectId(createdProjectId);
        Set<AssetDAO> addedAssets = assetRepository.findByProjectId(createdProjectId);

        boolean sameImportProjects =  (null != importFirsProjectId  && null != importAirsProjectId
            && importFirsProjectId.equals(importAirsProjectId));

        if (null == importFirsProjectId) {
            assertNull(importableFos, "Should be no importable fos");
            assertTrue(addedFos.size() == 0,"Should be no imported fos");
        } else {
            assertNotNull(importableFos, "Should be importable fos");
            assertTrue(addedFos.size() > 0,"Should be imported fos");
            assertEquals(importableFos.size(), addedFos.size(), "Incorrect number of fos found");
            // check firs counts
            for (FunctionalOutputDAO importableFo : importableFos) {
                // find matching added fo and see if we have same firs
                for (FunctionalOutputDAO addedFo : addedFos) {
                    if (importableFo.getDataDictionaryEntry().getId().equals(
                        addedFo.getDataDictionaryEntry().getId())
                    ) {
                        assertEquals(importableFo.getFirs().size(), addedFo.getFirs().size(),
                            "Firs counts do not match");
                        assertTrue(importableFo.getFirs().containsAll(addedFo.getFirs())
                            && addedFo.getFirs().containsAll(importableFo.getFirs()),
                            "Firs entries do not match");
                        // check for imported links
                        if (sameImportProjects) {
                            // expect the same links
                            assertEquals(importableFo.getAssets().size(), addedFo.getAssets().size(),
                                "Linked assets counts do not match");
                        } else {
                            assertEquals(0, addedFo.getAssets().size(),
                                "No linked assets expected");
                        }
                    }
                }
            }
        }


        if (null == importAirsProjectId) {
            assertNull(importableAssets, "Should be no importable assets");
            assertTrue(addedAssets.size() == 0,"Should be no imported assets");
        } else {
            assertNotNull(importableAssets, "Should be importable assets");
            assertTrue(addedAssets.size() > 0,"Should be imported assets");
            assertEquals(importableAssets.size(), addedAssets.size(), "Incorrect number of assets found");
            // check airs counts
            for (AssetDAO importableAsset : importableAssets) {
                // find matching added asset and see if we have same airs
                for (AssetDAO addedAsset : addedAssets) {
                    if (importableAsset.getDataDictionaryEntry().getId().equals(
                        addedAsset.getDataDictionaryEntry().getId())
                    ) {
                        assertEquals(importableAsset.getAirs().size(), addedAsset.getAirs().size(),
                            "Airs counts do not match");
                        assertTrue(importableAsset.getAirs().containsAll(addedAsset.getAirs())
                                && addedAsset.getAirs().containsAll(importableAsset.getAirs()),
                            "Airs entries do not match");
                    }
                }
            }
        }
        deleteProjectFn(createdProjectId);
    }

    @Test
    public void copySampleProject() throws Exception {
        System.out.println("Starting test copySampleProject");
        UUID copiedProjectId = createCopyOfSampleProject();
        deleteProjectFn(copiedProjectId);
    }

    private UUID createCopyOfSampleProject() throws Exception {
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
            .collect(Collectors.toSet());
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
        assertTrue(copiedProject.getName().startsWith(startOfCopiedProjectName));
        projectManager.compareProjects(sourceProject, copiedProject);
        return copiedProjectId;
    }

    @Test
    public void fetchSampleProject() {
        // fetch the sample project as the front-end would do
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

    @Test
    public void exportImportSameOrganisationTest() {
        // use ProjectImportExportHelper methods directly to exercise export and import

        List<ProjectDAO> projectDaos = projectRepository.findByName("Sample Project");
        assertEquals(1,projectDaos.size());
        ProjectDAO importedProject = null;
        try {
            // import to 'same' organisation
            String exportedFileData = projectImportExportHelper.exportProject(projectDaos.get(0).getId());
            String exportedData = null;
            try {
                exportedData = compressionHelper.decompress(Base64.getDecoder().decode(exportedFileData));
            } catch (Exception e) {
                fail(e);
            }
            System.out.println("Exported data = \n" + exportedData);
            // need to change project name to avoid clash
            try {
                JSONObject projectJson = new JSONObject(exportedData);
                String origProjectName = projectJson.getJSONObject("project").getString("project_name");
                projectJson.getJSONObject("project").put(
                    "project_name", origProjectName + System.currentTimeMillis());

                importedProject = projectImportExportHelper.importProjectFile(
                    Base64.getEncoder().encodeToString(compressionHelper.compress(projectJson.toString())));
                projectManager.compareProjects(projectDaos.get(0), importedProject);
            } catch (JSONException | IOException e) {
                fail(e);
            }
        } finally {
            if (null != importedProject) {
                projectManager.delete(importedProject.getId(), port);
            }
        }
    }

    private void checkProjectsWithSpecificDds(UUID foDictionaryId, int expectedProjectsWithFoDdOverBaseline,
                                              UUID assetDictionaryId, int expectedProjectsWithAssetDdOverBaseline) {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project/fodd/" + foDictionaryId.toString());
        try {
            final JSONArray jsonProjectsResponseRaw = new JSONArray(response.getBody());
            if (expectedProjectsWithFoDdOverBaseline < 0) {
                // save baseline so that further test test the incremental change
                this.projectsWithSampleProjectFoDictionary = jsonProjectsResponseRaw.length();
            } else {
                assertEquals(this.projectsWithSampleProjectFoDictionary
                             + expectedProjectsWithFoDdOverBaseline, jsonProjectsResponseRaw.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
        response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project/assetdd/" + assetDictionaryId.toString());
        try {
            final JSONArray jsonProjectsResponseRaw = new JSONArray(response.getBody());
            if (expectedProjectsWithAssetDdOverBaseline < 0) {
                // save baseline so that further test test the incremental change
                this.projectsWithSampleProjectAssetDictionary = jsonProjectsResponseRaw.length();
            } else {
                assertEquals(this.projectsWithSampleProjectAssetDictionary
                    + expectedProjectsWithAssetDdOverBaseline, jsonProjectsResponseRaw.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }

    }

    private static final String REPLACEMENT_ASSET_DD_NAME = "Replacement Asset DD";
    private static final String REPLACEMENT_ASSET_DD_NAME_2 = "Replacement Asset2 DD";
    private static final String REPLACEMENT_FO_DD_NAME = "Replacement FO DD";
    private static final String REPLACEMENT_FO_DD_NAME_2 = "Replacement FO2 DD";


    @Test
    public void exportImportAnotherOrganisationTest() {
        // this needs to exercise replacement POO creation and handle the 3 cases of Data Dictionary matching
        // i.e. match existing by name, match existing by entries and create new
        // modify json to make import to 'another' organisation and importation of both data dictionaries
        // will also exercise get projects with dd apis
        List<ProjectDAO> projectDaos = projectRepository.findByName("Sample Project");
        assertEquals(1,projectDaos.size());
        ProjectDAO importedProject2 = null;
        ProjectDAO importedProject3 = null;
        ProjectDAO importedProject4 = null;
        try {
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, -1,
                TestApiManager.sampleAssetDdId, -1);
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, 0,
                TestApiManager.sampleAssetDdId, 0);
            // import to 'another' organisation by changing the dd ids
            String exportedFileData = projectImportExportHelper.exportProject(projectDaos.get(0).getId());
            String exportedData = null;
            try {
                exportedData = compressionHelper.decompress(Base64.getDecoder().decode(exportedFileData));
            } catch (Exception e) {
                fail(e);
            }
            System.out.println("Exported data = \n" + exportedData);
            // this test should use the existing data dictionaries as they match by name
            exportedData = exportedData
                .replace(projectDaos.get(0).getFoDataDictionary().getId().toString(),
                    "12345678-1234-1234-1234-123456789012")
                .replace(projectDaos.get(0).getAssetDataDictionary().getId().toString(),
                    "12345678-1234-1234-1234-123456789012");
            try {
                // need to change project name to avoid clash
                JSONObject projectJson = new JSONObject(exportedData);
                String origProjectName = projectJson.getJSONObject("project").getString("project_name");
                projectJson.getJSONObject("project").put(
                    "project_name", origProjectName + System.currentTimeMillis());

                importedProject2 = projectImportExportHelper.importProjectFile(
                    Base64.getEncoder().encodeToString(compressionHelper.compress(projectJson.toString())));
            } catch (Exception e) {
                fail(e);
            }
            projectManager.compareProjects(projectDaos.get(0), importedProject2, null);
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, 1,
                TestApiManager.sampleAssetDdId, 1);
            // change the names of the dictionaries in the export file and check the existing dictionaries are matched
            // by matching the entries
            exportedData = exportedData
                .replace(projectDaos.get(0).getAssetDataDictionary().getName(), REPLACEMENT_ASSET_DD_NAME)
                .replace(projectDaos.get(0).getFoDataDictionary().getName(), REPLACEMENT_FO_DD_NAME);
            try {
                // need to change project name to avoid clash
                JSONObject projectJson = new JSONObject(exportedData);
                String origProjectName = projectJson.getJSONObject("project").getString("project_name");
                projectJson.getJSONObject("project").put(
                    "project_name", origProjectName + "X" + System.currentTimeMillis());
                importedProject3 = projectImportExportHelper.importProjectFile(
                    Base64.getEncoder().encodeToString(compressionHelper.compress(projectJson.toString())));
            } catch (Exception e) {
                fail(e);
            }
            projectManager.compareProjects(projectDaos.get(0),
                importedProject3, null);
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, 2,
                TestApiManager.sampleAssetDdId, 2);

            // change some of the entry ids of the entries in the export file so that new data dictionaries are created
            exportedData = exportedData
                .replace(REPLACEMENT_ASSET_DD_NAME, REPLACEMENT_ASSET_DD_NAME_2)
                .replace(REPLACEMENT_FO_DD_NAME, REPLACEMENT_FO_DD_NAME_2)
                // replace codes to force new dictionary
                // (do not change codes in use by project else test comparison will fail)
                .replace("EF_20_05", "XXX_20_05")
                .replace("Ss_15_30_33", "YYY_15_30_33");
            try {
                // need to change project name to avoid clash
                JSONObject projectJson = new JSONObject(exportedData);
                String origProjectName = projectJson.getJSONObject("project").getString("project_name");
                projectJson.getJSONObject("project").put(
                    "project_name", origProjectName + "Y" + System.currentTimeMillis());
                importedProject4 = projectImportExportHelper.importProjectFile(
                    Base64.getEncoder().encodeToString(compressionHelper.compress(projectJson.toString())));
            } catch (Exception e) {
                fail(e);
            }
            projectManager.compareProjects(projectDaos.get(0),
                importedProject4, null, REPLACEMENT_FO_DD_NAME_2, REPLACEMENT_ASSET_DD_NAME_2);
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, 2,
                TestApiManager.sampleAssetDdId, 2);
        } finally {
            if (null != importedProject2) {
                projectManager.delete(importedProject2.getId(), port);
            }
            if (null != importedProject3) {
                projectManager.delete(importedProject3.getId(), port);
            }
            if (null != importedProject4) {
                projectManager.delete(importedProject4.getId(), port);
            }
            FunctionalOutputDataDictionaryDAO foDdDao = foDdRepository.findByName(REPLACEMENT_FO_DD_NAME_2);
            if (null != foDdDao) {
                dataDictionaryManager.deleteFoDataDictionary(foDdDao.getId());
            }
            AssetDataDictionaryDAO assetDdDao = assetDdRepository.findByName(REPLACEMENT_ASSET_DD_NAME_2);
            if (null != assetDdDao) {
                dataDictionaryManager.deleteAssetDataDictionary(assetDdDao.getId());
            }
            this.checkProjectsWithSpecificDds(
                TestApiManager.sampleFoDdId, 0,
                TestApiManager.sampleAssetDdId, 0);
        }
    }
}

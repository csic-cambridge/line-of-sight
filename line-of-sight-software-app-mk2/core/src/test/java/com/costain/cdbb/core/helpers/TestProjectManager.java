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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


// This class manages projects for the integration tests
@Component
public class TestProjectManager {
    public static final String SAMPLE_PROJECT_ID = "387dac90-e188-11ec-8fea-0242ac120002";
    public static final String SAMPLE_PROJECT_NAME = "Sample Project";
    public static final boolean INCLUDE_OIR_AIR_LINKS = false;
    public static final boolean EXCLUDE_OIR_AIR_LINKS = !INCLUDE_OIR_AIR_LINKS;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    private TestOoManager ooManager;

    @Autowired
    FunctionalOutputRepository foRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestFunctionalOutputManager foManager;

    @Autowired
    TestAssetManager assetManager;

    public UUID create(String projectName, int port) throws Exception {
        return create(projectName, null, null, port);
    }

    public UUID create(String projectName, UUID foDdId, UUID assetDdId, int port)
        throws Exception {
        System.out.println("Starting test create");
        JSONObject projectJsonObject;
        projectJsonObject = new JSONObject();
        projectJsonObject.put("name", projectName + " test @" + System.currentTimeMillis());
        projectJsonObject.put("fo_dd_id",
            foDdId == null ? TestApiManager.sampleFoDdId : foDdId.toString());
        projectJsonObject.put("asset_dd_id",
            assetDdId == null ? TestApiManager.sampleAssetDdId : assetDdId.toString());

        HttpEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            projectJsonObject.toString(),
            "http://localhost:" + port + "/api/project");
        String projectResultAsJsonStr = response.getBody();

        JSONObject jsonObject = new JSONObject(projectResultAsJsonStr);
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

    public void delete(UUID projectId, int port) {
        //Delete the created project
        System.out.println("Starting test deleteProjectFn");
        Optional<ProjectDAO> optProject = projectId != null ? projectRepository.findById(projectId) : Optional.empty();
        if (optProject.isEmpty()) {
            System.out.println("Project " + projectId + " not found for deletion");
            return;
        }

        //TODO Using api,  add some functional requirements, [ to do : fos, firs, assets and airs]

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

    private boolean matchFrsNamesNotIds(
        ProjectDAO matchProject, Set<FunctionalRequirementDAO> frsToMatch, Set<FunctionalRequirementDAO> sourceFrs) {
        int result = (int) frsToMatch.stream().filter(
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

    private boolean findPooWithMatchingOoVersionFromPooList(
        // find poo with matching ooversion - check its frs have different ids but same name
        ProjectDAO matchProject, ProjectOrganisationalObjectiveDAO pooToMatchAgainstSource,
        Set<ProjectOrganisationalObjectiveDAO> sourcePoos) {
        List<ProjectOrganisationalObjectiveDAO> matchedPooList = sourcePoos.stream().filter(
            sourcePoo -> {
                System.out.println("Comparing project ids - " + sourcePoo.getProjectId() + " - "
                    + matchProject.getId());
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

    public void compareProjects(ProjectDAO sourceProject, ProjectDAO copiedProject, boolean excludeOirAirLinks) {
        compareProjects(sourceProject, copiedProject, null, null, null, excludeOirAirLinks);
    }

    public void compareProjects(ProjectDAO sourceProject, ProjectDAO copiedProject,
                                Integer pooOverrideCount, boolean excludeOirAirLinks) {
        compareProjects(sourceProject, copiedProject, pooOverrideCount, null, null, excludeOirAirLinks);
    }

    public void compareProjects(ProjectDAO sourceProject, ProjectDAO copiedProject, Integer pooOverrideCount,
                                String expectedFoDdName, String expectedAssetDdName,
                                boolean excludeOirAirLinks) {
        assertAll(
            () -> {
                String errorMsg = "FO Data Dictionary name incorrect"
                    + " - Warning: this failure may be because  there are equivalent FO "
                    + " Data Dictionaries in the database - please remove duplicates and re-run test";
                if (null == expectedFoDdName) {
                    assertEquals(sourceProject.getFoDataDictionary(), copiedProject.getFoDataDictionary(),
                        errorMsg);
                } else {
                    assertEquals(expectedFoDdName, copiedProject.getFoDataDictionary().getName(), errorMsg);
                }
            },
            () -> {
                String errorMsg = "Asset Data Dictionary name incorrect"
                    + " - Warning: this failure may be because  there are equivalent Asset "
                    + " Data Dictionaries in the database - please remove duplicates and re-run test";
                if (null == expectedAssetDdName) {
                    assertEquals(sourceProject.getAssetDataDictionary(), copiedProject.getAssetDataDictionary(),
                        errorMsg);
                } else {
                    assertEquals(expectedAssetDdName, copiedProject.getAssetDataDictionary().getName(), errorMsg);
                }
            },
            () -> assertEquals(null == pooOverrideCount
                    ? sourceProject.getProjectOrganisationalObjectiveDaos().size()
                    : pooOverrideCount,
                copiedProject.getProjectOrganisationalObjectiveDaos().size(),
                "Different number of POOs"),
            () -> assertEquals(sourceProject.getFunctionRequirementDaos().size(),
                copiedProject.getFunctionRequirementDaos().size(),
                "Different number of FRs")
        );
        if (!excludeOirAirLinks) {
            for (ProjectOrganisationalObjectiveDAO sourcePooDao
                : sourceProject.getProjectOrganisationalObjectiveDaos()) {
                for (ProjectOrganisationalObjectiveDAO copiedPooDao
                    : copiedProject.getProjectOrganisationalObjectiveDaos()) {
                    if (sourcePooDao.getOoVersion().getOo().getId()
                        .equals(copiedPooDao.getOoVersion().getOo().getId())) {
                        assertEquals(sourcePooDao.getFrs().size(), copiedPooDao.getFrs().size(),
                            "Poo links count to Frs does not match for OO id = "
                                + sourcePooDao.getOoVersion().getOo().getId()
                                + ", source = " + sourcePooDao.getFrs().size()
                                + ", copied = " + copiedPooDao.getFrs().size());
                    }
                }
            }
            // check Poos using ooVersions and frs under each (already tested for same number)
            int matchingPoos = (int) sourceProject.getProjectOrganisationalObjectiveDaos().stream().filter(sourceDao ->
                findPooWithMatchingOoVersionFromPooList(
                    copiedProject, sourceDao, copiedProject.getProjectOrganisationalObjectiveDaos())
            ).count();
        }

        //       assertEquals(matchingPoos, null == pooOverrideCount
        //           ? sourceProject.getProjectOrganisationalObjectiveDaos().size()
        //           : pooOverrideCount,
        //           "Unexpected number of copied Poos");

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
        sourceFos.stream().forEach(foDao -> sourceFosMap.put(foDao.getDataDictionaryEntry().getEntryId(), foDao));
        Map<String, FunctionalOutputDAO> copiedFosMap = new HashMap<>();
        copiedFos.stream().forEach(foDao -> copiedFosMap.put(foDao.getDataDictionaryEntry().getEntryId(), foDao));
        sourceFos.stream().forEach(foDao ->
            assertTrue(foManager.compareFirsDaos(
                sourceFosMap.get(foDao.getDataDictionaryEntry().getEntryId()).getFirs(),
                copiedFosMap.get(foDao.getDataDictionaryEntry().getEntryId()).getFirs())));


        // Data Dictionaries - Assets
        Set<AssetDAO> sourceAssets = assetRepository.findByProjectId(sourceProject.getId());
        Set<AssetDAO> copiedAssets = assetRepository.findByProjectId(copiedProject.getId());
        assertEquals(sourceAssets.size(), copiedAssets.size(), "Total number of Assets does not match");

        // check airs counts
        Map<String, AssetDAO> sourceAssetsMap = new HashMap<>();
        sourceAssets.stream().forEach(assetDao ->
            sourceAssetsMap.put(assetDao.getDataDictionaryEntry().getEntryId(), assetDao));
        Map<String, AssetDAO> copiedAssetsMap = new HashMap<>();
        copiedAssets.stream().forEach(assetDao ->
            copiedAssetsMap.put(assetDao.getDataDictionaryEntry().getEntryId(), assetDao));
        sourceAssets.stream().forEach(assetDao -> {
            assertTrue(assetManager.compareAirsDaoStrings(
                sourceAssetsMap.get(assetDao.getDataDictionaryEntry().getEntryId()).getAirs(),
                copiedAssetsMap.get(assetDao.getDataDictionaryEntry().getEntryId()).getAirs()));
            assertTrue(excludeOirAirLinks || compareOirsOfAirs(
                sourceAssetsMap.get(assetDao.getDataDictionaryEntry().getEntryId()).getAirs(),
                copiedAssetsMap.get(assetDao.getDataDictionaryEntry().getEntryId()).getAirs()));
        });

    }

    public UUID createCopyOfSampleProject(int port) throws Exception {

        String copiedProjectName = "New Copy Of " + SAMPLE_PROJECT_NAME + "-" + System.currentTimeMillis();
        Optional<ProjectDAO> optProject = projectRepository.findById(UUID.fromString(SAMPLE_PROJECT_ID));
        assertTrue(optProject.isPresent(), "Source project " + SAMPLE_PROJECT_ID + " not found for copying");
        UUID copiedProjectId = optProject.get().getId();
        JSONObject copyProjectJsonObject = new JSONObject();
        copyProjectJsonObject.put("name", copiedProjectName);
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            copyProjectJsonObject.toString(),
            "http://localhost:" + port + "/api/project/pid/" + SAMPLE_PROJECT_ID);
        String personResultAsJsonStr = response.getBody();

        JSONObject jsonObject = new JSONObject(personResultAsJsonStr);
        assertTrue(jsonObject.has("id"), "Create project response has no id field");

        copiedProjectId = UUID.fromString((String) jsonObject.get("id"));

        String newProjectName = (String) jsonObject.get("name");
        Set<OrganisationalObjectiveDAO> ooDaos =
            StreamSupport.stream(ooRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(copiedProjectId);

        UUID finalCopiedProjectId = copiedProjectId;
        assertAll(
            () -> assertEquals(ooDaos.size(), pooDaos.size(),
                "Expected " + ooDaos.size() + " poos but found " + pooDaos.size()),
            () -> assertTrue(jsonObject.has("name"), "Create project response has no name field"),

            () -> assertNotEquals(SAMPLE_PROJECT_ID, finalCopiedProjectId.toString()),
            () -> assertEquals(copiedProjectName, newProjectName)
        );
        System.out.println("Project " + SAMPLE_PROJECT_NAME + " with Id " + SAMPLE_PROJECT_ID
            + " was copied to project " + newProjectName + " with Id " + copiedProjectId);

        // test copied project by fetching properties from database
        ProjectDAO copiedProject = projectRepository.findById(copiedProjectId).get();
        ProjectDAO sourceProject = optProject.get();
        System.out.println("Source project = " + sourceProject);
        System.out.println("Copied project = " + copiedProject);
        assertTrue(copiedProject.getName().startsWith(copiedProjectName));
        this.compareProjects(sourceProject, copiedProject, INCLUDE_OIR_AIR_LINKS);
        return copiedProjectId;
    }

    private boolean compareOirsOfAirs(Set<AirsDAO> airs1, Set<AirsDAO> airs2) {
        if (airs1 == null && airs2 == null) {
            return true;
        }
        if (airs1 == null || airs2 == null) {
            return false;
        }
        if (airs1.size() != airs2.size()) {
            return false;
        }
        // can only compare the strings of the oirs as they must have different ids
        // set up oirs1 and oirs2 for each airs string and then match them
        Map<String, AirsOirs> airsOirs12 = new HashMap<>();
        for (AirsDAO airsDao : airs1) {
            AirsOirs airsOirs = new AirsOirs();
            for (OirDAO oirsDao : airsDao.getOirs()) {
                airsOirs.addOirs1(oirsDao.getOirs());
            }
            airsOirs12.put(airsDao.getAirs(), airsOirs);
        }
        // now find matching airs dao and put Oirs into airsOirs12
        for (AirsDAO airsDao : airs2) {
            AirsOirs airsOirs = airsOirs12.get(airsDao.getAirs());
            if (null == airsOirs) {
                System.err.println("Could not find match for airs2 = " + airsDao.getAirs() + " in airs1");
                return false;
            }
            for (OirDAO oirsDao : airsDao.getOirs()) {
                airsOirs.addOirs2(oirsDao.getOirs());
            }
            // now do match of both sets Oirs strings
            if (!(airsOirs.oirs1.containsAll(airsOirs.oirs2) && airsOirs.oirs2.containsAll(airsOirs.oirs1))) {
                System.err.println("failed to match Oirs1 with Oirs2 for Airs " + airsDao.getAirs());
                return false;
            }
            airsOirs12.remove(airsDao.getAirs());
        }
        return airsOirs12.isEmpty();
    }

    public static class AirsOirs {
        List<String> oirs1 = new ArrayList<>();
        List<String> oirs2 = new ArrayList<>();

        public void addOirs1(String oirs) {
            oirs1.add(oirs);
        }

        public void addOirs2(String oirs) {
            oirs2.add(oirs);
        }

    }
}



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
import com.costain.cdbb.core.helpers.TestAssetManager;
import com.costain.cdbb.core.helpers.TestFunctionalOutputManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.Firs;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
public class FunctionalOutputsRestTest {
    @LocalServerPort
    private int port;
    private static UUID projectId;
    private static FunctionalOutputDataDictionaryDAO foDdDao;
    private static AssetDataDictionaryDAO assetDdDao;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    FunctionalOutputRepository functionalOutputRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository functionalOutputDdeRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDdeRepository;

    @Autowired
    TestFunctionalOutputManager foManager;

    @Autowired
    TestAssetManager assetManager;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    private TestProjectManager projectManager;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            foDdDao = foManager.createFoDdWithEntry(projectId, port);
            assetDdDao = assetManager.createAssetDdWithTwoEntries();
            projectId = projectManager
                .create("FunctionalOutputsTest", foDdDao.getId(), assetDdDao.getId(), port);
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
            assetManager.deleteAssetDictionary(assetDdDao.getId());
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
    }

    // test functionalOutputs:  get, functionalOutputs/{id}: get,put,delete
    private void getAllFunctionalOutputs() {
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId);
        // process result when fewer entries (note: parallel test may be creating these at the same time)
        String ddResultAsJsonStr = response.getBody();
        System.out.println("Get all functionalOutputs for project " + projectId + " response (1):" + ddResultAsJsonStr);

        try {
            JSONArray functionalOutputsFromResponse = new JSONArray(ddResultAsJsonStr);
            Set<FunctionalOutputDAO> functionalOutputsFromDb = functionalOutputRepository.findByProjectId(projectId);
            assertEquals(functionalOutputsFromDb.size(), functionalOutputsFromResponse.length());
        } catch (JSONException e) {
            fail(e);
        }
        // now create a functionalOutput and fetch
        try {
            final FunctionalOutputDAO functionalOutput = foManager.createFunctionalOutput(projectId, port);
            response = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId);
            // process result when no entries
            ddResultAsJsonStr = response.getBody();
            System.out.println("Get all functionalOutputs for project " + projectId + " response(2): "
                + ddResultAsJsonStr);

            JSONArray functionalOutputsFromResponse = new JSONArray(ddResultAsJsonStr);
            Set<FunctionalOutputDAO> functionalOutputsFromDb = functionalOutputRepository.findByProjectId(projectId);
            assertEquals(functionalOutputsFromDb.size(), functionalOutputsFromResponse.length());
            assertEquals(1, functionalOutputsFromResponse.length());
            foManager.deleteFunctionalOutput(projectId, port, functionalOutput);
        } catch (JSONException e) {
            fail(e);
        }
    }

    // Note: getFunctionalOutput() is not part of the api

    @Test
    public void updateFunctionalOutput() {
        // check current fos first
        getAllFunctionalOutputs();

        List<AssetDAO> assetDaos = new ArrayList<>();
        List<AssetDataDictionaryEntryDAO> ddEntryDaos =
            new ArrayList<>(assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(assetDdDao.getId()));
        try {
            assetDaos.add(assetManager.createAsset(projectId, port, "Asset1 Fir Update", ddEntryDaos.get(0)));
            assetDaos.add(assetManager.createAsset(projectId, port, "Asset2 Fir Update", ddEntryDaos.get(1)));
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }

        // update the FIRs and assets
        try {
            final FunctionalOutputDAO functionalOutput = foManager.createFunctionalOutput(projectId, port);
            List<Firs> sourceFirs = new ArrayList<>();
            List<String> sourceAssets = new ArrayList<>();
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // add some firs
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 1"));
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 2"));
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // add some assets
            sourceAssets.add(assetDaos.get(0).getId().toString());
            sourceAssets.add(assetDaos.get(1).getId().toString());
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // remove a fir
            sourceFirs.remove(1);
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // remove an asset
            sourceAssets.remove(1);
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // add some firs and assets
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 2"));
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 3"));
            sourceAssets.add(assetDaos.get(1).getId().toString());
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // add some firs, remove some assets
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 4"));
            sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("fir 5"));
            sourceAssets.remove(0);
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // remove some firs, add some assets
            sourceFirs.remove(0);
            sourceAssets.add(assetDaos.get(0).getId().toString());
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);



            // remove some firs and assets
            sourceFirs.remove(1);
            sourceAssets.remove(0);
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            // remove all firs and assets
            sourceFirs.clear();
            sourceAssets.clear();
            updateFoWith(functionalOutput.getId(), sourceFirs, sourceAssets);

            foManager.deleteFunctionalOutput(projectId, port, functionalOutput);
        } catch (JSONException e) {
            fail(e);
        }
    }

    private void updateFoWith(UUID foId, List<Firs> sourceFirs, List<String> sourceAssetIds) {
        // update the fo with the given firs and assets and check update has occurred
        List<FunctionalOutputDataDictionaryEntryDAO> ddEntryDaos =
            new ArrayList<>(functionalOutputDdeRepository
                .findByFoDictionaryIdOrderByEntryId(foManager.getFunctionalOutputDd().getId()));
        try {

            Map<String, Object> ddeMap = new HashMap<>();
            ddeMap.put("entry_id", ddEntryDaos.get(0).getEntryId());
            ddeMap.put("text", "blah blah");
            Map<String, Object> map = new HashMap<>();
            map.put("data_dictionary_entry", ddeMap);
            map.put("firs", sourceFirs);
            map.put("assets", sourceAssetIds);
            String payload = new ObjectMapper().writeValueAsString(map);

            ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
                payload,
                "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId
                    + "/" + foId);
            // process result
            String ddResultAsJsonStr = response.getBody();
            // build FunctionalOutputDAO result from response
            JSONObject functionalOutputJsonObject = new JSONObject(ddResultAsJsonStr);
            JSONObject ddeJsonObject = functionalOutputJsonObject.getJSONObject("data_dictionary_entry");
            FunctionalOutputDataDictionaryEntryDAO functionalOutputDataDictionaryEntryDao =
                FunctionalOutputDataDictionaryEntryDAO.builder()
                .entryId(ddeJsonObject.getString("entry_id")).foDictionaryId(foManager.getFunctionalOutputDd().getId())
                .text(ddeJsonObject.getString("text"))
                .build();
            JSONArray firsJsonArray = functionalOutputJsonObject.getJSONArray("firs");
            Set<FirsDAO> firs = new HashSet<>();
            for (int i = 0; i < firsJsonArray.length(); i++) {
                JSONObject jsonFirs = (JSONObject)firsJsonArray.get(i);
                firs.add(FirsDAO.builder().id(jsonFirs.getString("id"))
                    .firs(jsonFirs.getString("firs")).build());
            }

            JSONArray assetsJsonArray = functionalOutputJsonObject.getJSONArray("assets");
            Set<String> assets = new HashSet<>();
            for (int i = 0; i < assetsJsonArray.length(); i++) {
                assets.add((String)(assetsJsonArray.get(i)));
            }

            assertAll(
                () -> assertEquals(functionalOutputDataDictionaryEntryDao.getEntryId(),
                    ddEntryDaos.get(0).getEntryId(), "Comparing entry ids"),
                () -> assertEquals(functionalOutputDataDictionaryEntryDao.getText(),
                    ddEntryDaos.get(0).getEntryId() + "-" + ddEntryDaos.get(0).getText(), "Comparing text"),
                () -> assertEquals(functionalOutputDataDictionaryEntryDao.getFoDictionaryId(),
                    foManager.getFunctionalOutputDd().getId(), "Comparing fo dd"),
                () -> assertEquals(sourceFirs.size(), firs.size(), "Comparing FIRs size"),
                () -> assertTrue(foManager.compareFirsStrings(firs, new HashSet<>(sourceFirs)),
                    "Comparing FIR strings"),
                () -> assertEquals(sourceAssetIds.size(), assets.size(), "Comparing assets size"),
                () -> assertTrue(assets.containsAll(sourceAssetIds)
                    && sourceAssetIds.containsAll(assets), "Comparing assets content")
            );
        } catch (JSONException | JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void createAndDeleteFunctionalOutput() {
        try {
            FunctionalOutputDAO functionalOutput = foManager.createFunctionalOutput(projectId, port);
            foManager.deleteFunctionalOutput(projectId, port, functionalOutput);
        } catch (JSONException e) {
            fail(e);
        }
    }

    @Test
    public void fetchAllFirs() {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/firs");
        // process result
        String ddResultAsJsonStr = response.getBody();
        try {
            JSONArray firs = new JSONArray(ddResultAsJsonStr);
            // get firs from all fos
            Set<FirsDAO> firsFromDb = new HashSet<>(); //TreeSet<>();
            Iterable<FunctionalOutputDAO> fos = functionalOutputRepository.findAll();
            for (FunctionalOutputDAO fo : fos) {
                for (FirsDAO firDao : fo.getFirs()) {
                    firsFromDb.add(firDao);
                }
            }
            assertEquals(firsFromDb.size(), firs.length());
            System.out.println("Not testing for FIRS returned in alphabetical order");
            // Prove firs in alphabetical order

            /*List<String> sortedFirsFromDb = new ArrayList<String>(firsFromDb);
            Collections.sort(sortedFirsFromDb, String.CASE_INSENSITIVE_ORDER);
            // compare response
            assertEquals(sortedFirsFromDb.size(), firs.length());
            int i = 0;
            for (String fromDb : sortedFirsFromDb) {
                assertEquals(fromDb, firs.getString(i++));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
    }
}

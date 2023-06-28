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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestAssetManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.Airs;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AirsRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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


public class AssetsRestTest {
    @LocalServerPort
    private int port;
    private static UUID projectId;
    private static AssetDataDictionaryDAO assetDdDao;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AirsRepository airsRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDdeRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestAssetManager assetManager;

    @BeforeAll
    public void runBeforeTestsBegin() {
        assetDdDao = assetManager.createAssetDdWithTwoEntries();
        try {
            projectId = projectManager.create("AssetsTest", null, assetDdDao.getId(), port);
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
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
        assetManager.deleteAssetDictionary(assetDdDao.getId());
    }

    // test assets: post, get, assets/{id}: get,put,delete




    private void deleteAsset(AssetDAO asset) {
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/assets/pid/" + projectId + "/" + asset.getId());
        // check it has been deleted from database
        assertFalse(assetRepository.findById(asset.getId()).isPresent());
    }

    @Test
    public void getAllAssets() {
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/assets/pid/" + projectId);
        // process result when no assets available
        String ddResultAsJsonStr = response.getBody();
        System.out.println("Get all assets response: " + ddResultAsJsonStr);

        try {
            JSONArray assetsFromResponse = new JSONArray(ddResultAsJsonStr);
            Set<AssetDAO> assetsFromDb = assetRepository.findByProjectId(projectId);
            assertEquals(assetsFromResponse.length(), assetsFromDb.size());
            assertEquals(assetsFromResponse.length(), 0);
        } catch (JSONException e) {
            fail(e);
        }
        // now create an asset and fetch
        try {
            List<AssetDataDictionaryEntryDAO> ddEntryDaos =
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(assetDdDao.getId()));
            final AssetDAO asset = assetManager.createAsset(projectId, port,
                "Fir Test", ddEntryDaos.get(0));
            response = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/assets/pid/" + projectId);
            // process result when no entries
            ddResultAsJsonStr = response.getBody();
            System.out.println("Get all assets response: " + ddResultAsJsonStr);

            JSONArray assetsFromResponse = new JSONArray(ddResultAsJsonStr);
            Set<AssetDAO> assetsFromDb = assetRepository.findByProjectId(projectId);
            assertEquals(assetsFromDb.size(), assetsFromResponse.length());
            assertEquals(1, assetsFromResponse.length());
            deleteAsset(asset);
        } catch (JSONException e) {
            fail(e);
        }
    }

    // Note: getAsset() is not part of the api

    @Test
    public void updateAsset() {
        // can only update the AIRs
        try {
            List<AssetDataDictionaryEntryDAO> ddEntryDaos =
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(assetDdDao.getId()));
            final AssetDAO asset = assetManager.createAsset(projectId, port, "Air Update", ddEntryDaos.get(0));

            // now change the airs and do an update
            // need to specify all the airs required (new and old)

            Set<AirsDAO> existingAirsDaos = asset.getAirs();
            Set<Airs> sourceAirs = new HashSet<>(existingAirsDaos.size());
            for (AirsDAO airDao : existingAirsDaos) {
                sourceAirs.add(new Airs().id(airDao.getId().toString()).airs(airDao.getAirs()));
                break; //include just 1 of existing airs
            }
            // add 2 new airs
            sourceAirs.add(new Airs().id(AirsDAO.NEW_ID).airs("New AirTest1"));
            sourceAirs.add(new Airs().id(AirsDAO.NEW_ID).airs("New AirTest2"));

            Map<String, Object> ddeMap = new HashMap<>();
            ddeMap.put("entry_id", ddEntryDaos.get(0).getEntryId());
            ddeMap.put("text", "abc");
            Map<String, Object> map = new HashMap<>();
            map.put("data_dictionary_entry", ddeMap);
            map.put("airs", sourceAirs.toArray());
            String payload = new ObjectMapper().writeValueAsString(map);

            ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
                payload,
                "http://localhost:" + port + "/api/assets/pid/" + projectId + "/" + asset.getId());
            // process result
            String ddResultAsJsonStr = response.getBody();
            // build AssetDAO result from response
            JSONObject assetJsonObject = new JSONObject(ddResultAsJsonStr);
            JSONObject ddeJsonObject = assetJsonObject.getJSONObject("data_dictionary_entry");
            AssetDataDictionaryEntryDAO assetDataDictionaryEntryDao = AssetDataDictionaryEntryDAO.builder()
                .entryId(ddeJsonObject.getString("entry_id"))
                .assetDictionaryId(assetManager.getAssetDd().getId())
                .text(ddeJsonObject.getString("text"))
                .build();
            JSONArray airsJsonArray = assetJsonObject.getJSONArray("airs");
            Set<AirsDAO> fetchedAirs = new HashSet<>();
            for (int i = 0; i < airsJsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) airsJsonArray.get(i);
                fetchedAirs.add(AirsDAO.builder().id(jsonObject.getString("id"))
                    .airs(jsonObject.getString("airs")).build());
            }
            AssetDAO result = AssetDAO.builder()
                .id(UUID.fromString(assetJsonObject.getString("id")))
                .dataDictionaryEntry(assetDataDictionaryEntryDao)
                .airs(fetchedAirs)
                .build();
            assertAll(
                () -> assertEquals(assetDataDictionaryEntryDao.getEntryId(), ddEntryDaos.get(0).getEntryId(),
                    "Comparing entry ids"),
                () -> assertEquals(assetDataDictionaryEntryDao.getText(),
                    ddEntryDaos.get(0).getEntryId() + "-" + ddEntryDaos.get(0).getText(),
                    "comparing Text"),
                () -> assertEquals(assetDataDictionaryEntryDao.getAssetDictionaryId(),
                    assetManager.getAssetDd().getId(), "Comparing dd ids"),
                () -> assertEquals(result.getAirs().size(), sourceAirs.size(), "Comparing AIRS size"),
                () -> assertTrue(assetManager.compareAirsStrings(result.getAirs(), sourceAirs),
                    "Comparing AIRS strings")
            );
            deleteAsset(asset);
        } catch (JSONException | JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void createAndDeleteAsset() {
        try {
            List<AssetDataDictionaryEntryDAO> ddEntryDaos =
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(assetDdDao.getId()));
            AssetDAO asset = assetManager.createAsset(projectId, port, "Asset Create", ddEntryDaos.get(0));
            deleteAsset(asset);
        } catch (JSONException e) {
            fail(e);
        }
    }

    @Test
    public void fetchAllAirs() {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/airs");
        // process result
        String ddResultAsJsonStr = response.getBody();
        try {
            JSONArray airs = new JSONArray(ddResultAsJsonStr);
            // get airs from all assets
            Set<AirsDAO> airsFromDb = new HashSet<>(); //new TreeSet<>();
            Iterable<AssetDAO> assetsFromDb = assetRepository.findAll();
            for (AssetDAO asset : assetsFromDb) {
                for (AirsDAO airDao : asset.getAirs()) {
                    airsFromDb.add(airDao);
                }
            }
            assertEquals(airsFromDb.size(), airs.length());
            System.out.println("Not testing for AIRS returned in alphabetical order");
            // Prove airs in alphabetical order
            //List<String> sortedAirsFromDb = new ArrayList<String>(airsFromDb);
            //Collections.sort(sortedAirsFromDb, String.CASE_INSENSITIVE_ORDER);
            // compare response
            /*
            int i = 0;
            for (String fromDb : sortedAirsFromDb) {
                assertEquals(fromDb, airs.getString(i++));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    private AirsDAO makeAirsDao(int n) {
        AirsDAO airsDao = AirsDAO.builder().airs("AIR #" + n).build();
        return airsDao;
    }

    private AssetDataDictionaryEntryDAO getAdde() {
        AssetDataDictionaryEntryDAO adde =
            assetDdeRepository.findByAssetDictionaryIdAndEntryId(
                UUID.fromString("e1970a24-e8c7-11ec-8fea-0242ac120002"), "Ss_15_10_30_29");

        if (null == adde) {
            adde = assetDdeRepository.save(AssetDataDictionaryEntryDAO.builder()
                .entryId("Ss_15_10_30_29").text("Earthworks filling systems around trees")
                .assetDictionaryId(UUID.fromString("e1970a24-e8c7-11ec-8fea-0242ac120002"))
                .build());
        }
        return adde;
    }

    @Test
    public void basicCreateAndDeleteAsset() {
        UUID sampleProjectId = UUID.fromString("387dac90-e188-11ec-8fea-0242ac120002");

        AirsDAO air1 = makeAirsDao(1);
        AirsDAO air2 = makeAirsDao(2);
        Set<AirsDAO> airs = new HashSet<>();
        airs.add(air1);
        airs.add(air2);
        AssetDAO asset = AssetDAO.builder().projectId(sampleProjectId)
            .dataDictionaryEntry(getAdde()).airs(airs).build();
        asset = assetRepository.save(asset);

        assetRepository.deleteById(asset.getId());
    }
}

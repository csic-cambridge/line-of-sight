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
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.google.gson.GsonBuilder;
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


public class AssetsWebTest {
    @LocalServerPort
    private int port;
    private static UUID projectId;
    private static AssetDataDictionaryDAO assetDdDao;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDdeRepository;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestAssetManager assetManager;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            projectId = projectManager.create("AssetsTest", port);
        } catch (Exception e) {
            fail("Failed to create initial project" + e);
        }
        assetDdDao = assetManager.createAssetDdWithTwoEntries();
    }

    @BeforeEach
    public void runBeforeAllTestMethods() {

    }

    @AfterAll
    public void runAfterAllTestsComplete() {
        assetManager.deleteAssetDictionary(assetDdDao.getId());
        try {
            projectManager.delete(projectId, port);
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
    }

    // test assets: post, get, assets/{id}: get,put,delete




    private void deleteAsset(AssetDAO asset) {
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/assets/" + projectId + "/" + asset.getId());
        // check it has been deleted from database
        assertFalse(assetRepository.findById(asset.getId()).isPresent());
    }

    @Test
    public void getAllAssets() {
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/assets/" + projectId);
        // process result when no entries
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
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryId(assetDdDao.getId()));
            final AssetDAO asset = assetManager.createAsset(projectId, port,
                "Fir Test", ddEntryDaos.get(0));
            response = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + "/api/assets/" + projectId);
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
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryId(assetDdDao.getId()));
            final AssetDAO asset = assetManager.createAsset(projectId, port, "Fir Update", ddEntryDaos.get(0));

            // now change the airs and do an update
            Set<String> sourceAirs = new HashSet<>();
            sourceAirs.add("New AirTest1");
            sourceAirs.add("New AirTest2");

            Map<String, Object> ddeMap = new HashMap<>();
            ddeMap.put("id", ddEntryDaos.get(0).getId());
            ddeMap.put("text", "");
            Map<String, Object> map = new HashMap<>();
            map.put("data_dictionary_entry", ddeMap);
            map.put("airs", sourceAirs.toArray());
            String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);

            ResponseEntity<String> response = apiManager.doSuccessfulPutApiRequest(
                payload,
                "http://localhost:" + port + "/api/assets/" + projectId + "/" + asset.getId());
            // process result
            String ddResultAsJsonStr = response.getBody();
            // build AssetDAO result from response
            JSONObject assetJsonObject = new JSONObject(ddResultAsJsonStr);
            JSONObject ddeJsonObject = assetJsonObject.getJSONObject("data_dictionary_entry");
            AssetDataDictionaryEntryDAO assetDataDictionaryEntryDao = AssetDataDictionaryEntryDAO.builder()
                .id(ddeJsonObject.getString("id"))
                .assetDictionaryId(assetManager.getAssetDd().getId())
                .text(ddeJsonObject.getString("text"))
                .build();
            JSONArray airsJsonArray = assetJsonObject.getJSONArray("airs");
            Set<String> airs = new HashSet<>();
            for (int i = 0; i < airsJsonArray.length(); i++) {
                airs.add((String)airsJsonArray.get(i));
            }
            AssetDAO result = AssetDAO.builder()
                .id(UUID.fromString(assetJsonObject.getString("id")))
                .dataDictionaryEntry(assetDataDictionaryEntryDao)
                .airs(airs)
                .build();
            assertAll(
                () -> assertEquals(assetDataDictionaryEntryDao.getId(), ddEntryDaos.get(0).getId()),
                () -> assertEquals(assetDataDictionaryEntryDao.getText(),
                    ddEntryDaos.get(0).getId() + "-" + ddEntryDaos.get(0).getText()),
                () -> assertEquals(assetDataDictionaryEntryDao.getAssetDictionaryId(),
                    assetManager.getAssetDd().getId()),
                () -> assertEquals(result.getAirs().size(), sourceAirs.size()),
                () -> assertTrue(result.getAirs().containsAll(sourceAirs)
                    && sourceAirs.containsAll(result.getAirs()))
            );
            deleteAsset(asset);
        } catch (JSONException e) {
            fail(e);
        }
    }

    @Test
    public void createAndDeleteAsset() {
        try {
            List<AssetDataDictionaryEntryDAO> ddEntryDaos =
                new ArrayList<>(assetDdeRepository.findByAssetDictionaryId(assetDdDao.getId()));
            AssetDAO asset = assetManager.createAsset(projectId, port, "Fir Create", ddEntryDaos.get(0));
            deleteAsset(asset);
        } catch (JSONException e) {
            fail(e);
        }
    }
}

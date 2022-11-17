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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestDataDictionaryManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.helpers.CompressionHelper;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.test.context.ActiveProfiles;






@ActiveProfiles("no_security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class ImportExportRestTest {
    @LocalServerPort
    private int port;
    private static UUID project1Id;
    private static UUID importedProjectId;
    private List<Map<String, Object>> metaDataList = new ArrayList<>();



    private static final String META_DATA_KEY_URL = "url";
    private static final String META_DATA_KEY_NAME = "name";
    private static final String META_DATA_KEY_DD_NAME = "ddName";
    private static final String META_DATA_KEY_IMPORTED_DD_ID = "importedDdId";
    @Autowired
    private TestApiManager apiManager;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    private TestDataDictionaryManager dataDictionaryManager;

    @Autowired
    private AssetDataDictionaryRepository assetDdRepository;

    @Autowired
    private FunctionalOutputDataDictionaryRepository foDdRepository;

    @Autowired
    CompressionHelper compressionHelper;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            project1Id = projectManager.create("Project User Permissions Test1", port);
        } catch (Exception e) {
            fail("Failed to create initial project(s)" + e);
        }
    }

    @AfterAll
    public void runAfterTestsFinish() {
        try {
            projectManager.delete(project1Id, port);
            if (importedProjectId != null) {
                projectManager.delete(importedProjectId, port);
            }
            for (Map<String, Object> metaData : metaDataList) {
                if (metaData.get(META_DATA_KEY_NAME).equals("Asset")) {
                    if (metaData.get(META_DATA_KEY_IMPORTED_DD_ID) != null) {
                        dataDictionaryManager
                            .deleteAssetDataDictionary((UUID) metaData.get(META_DATA_KEY_IMPORTED_DD_ID));
                    }
                } else if (metaData.get(META_DATA_KEY_NAME).equals("FO")) {
                    if (metaData.get(META_DATA_KEY_IMPORTED_DD_ID) != null) {
                        dataDictionaryManager
                            .deleteFoDataDictionary((UUID) metaData.get(META_DATA_KEY_IMPORTED_DD_ID));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to delete initial project(s) or data dictionaries" + e);
        }
    }


    /* @Test
    public void importProject() {
        // Only works with dummy data at present
        HttpEntity<String> response = apiManager.doSuccessfulPostApiRequestWithOctetStream(
            Base64.getEncoder().encodeToString("test project data".getBytes(StandardCharsets.UTF_8)),
            "http://localhost:" + port + "/api/project/import");
        String projectResultAsJsonStr = response.getBody();
        // TODO need to set importedProjectId so it can be deleted at end of test
        try {
            JSONObject jsonObject = new JSONObject(projectResultAsJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    public void exportProject() {
        // Only works with dummy data at present
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project/export/pid/" + project1Id.toString());
        String projectResultAsBase64Str = response.getBody();
        String exportFileStr =
            new String(Base64.getDecoder().decode(projectResultAsBase64Str), StandardCharsets.UTF_8);
        System.out.println("Export data = " + exportFileStr);
    }*/

    @Test
    public void importAssetAndFoDataDictionaries() {
        metaDataList = new ArrayList<>();
        Map<String, Object> assetMetaData = new HashMap<>();
        Map<String, Object> foMetaData = new HashMap<>();
        metaDataList.add(assetMetaData);
        metaDataList.add(foMetaData);

        assetMetaData.put(META_DATA_KEY_URL, "/api/asset-data-dictionary");
        assetMetaData.put(META_DATA_KEY_NAME, "Asset");
        assetMetaData.put(META_DATA_KEY_DD_NAME, "ImportAssetDDTest " + System.currentTimeMillis());

        foMetaData.put(META_DATA_KEY_URL, "/api/functional-output-data-dictionary");
        foMetaData.put(META_DATA_KEY_NAME, "FO");
        foMetaData.put(META_DATA_KEY_DD_NAME, "ImportFODDTest " + System.currentTimeMillis());

        final int entryCount = 10;

        for (Map<String, Object> metaData : metaDataList) {
            StringBuilder testAssetDdSb = new StringBuilder();
            for (int i = 0; i < entryCount; i++) {
                testAssetDdSb
                    .append("Code ")
                    .append(i)
                    .append(",")
                    .append("\"Comma ,")
                    .append(metaData.get("name"))
                    .append(" ")
                    .append(i)
                    .append("\"\n");
            }
            String url = (String)metaData.get(META_DATA_KEY_URL);
            String base64CompressedData = null;
            try {
                base64CompressedData = Base64.getEncoder().encodeToString(
                    compressionHelper.compress((metaData.get(META_DATA_KEY_DD_NAME)
                        + "\n" + testAssetDdSb.toString())));
            } catch (Exception e) {
                fail(e);
            }
            HttpEntity<String> response = apiManager.doSuccessfulPostApiRequestWithOctetStream(
                base64CompressedData,
                "http://localhost:" + port + url);
            String ddResultAsJsonStr = response.getBody();

            try {
                JSONObject jsonObject = new JSONObject(ddResultAsJsonStr);

                // check dictionary created correctly
                metaData.put(META_DATA_KEY_IMPORTED_DD_ID, UUID.fromString(jsonObject.getString("id")));
                dataDictionaryManager.fetchDataDictionaryList(
                    url, port, (String)metaData.get(META_DATA_KEY_DD_NAME),
                    (UUID)metaData.get(META_DATA_KEY_IMPORTED_DD_ID));
                JSONArray entriesJsonArray =
                    dataDictionaryManager.fetchDataDictionaryEntries(
                        url, port, "", (UUID)metaData.get(META_DATA_KEY_IMPORTED_DD_ID), entryCount);
                for (int i = 0; i < entriesJsonArray.length(); i++) {
                    JSONObject entry = entriesJsonArray.getJSONObject(i);
                    // assume in order
                    assertEquals("Code " + i, entry.getString("entry_id"),
                        "Unexpected id in " +  metaData.get(META_DATA_KEY_NAME) + " dd entry");
                    assertEquals("Code " + i + "-Comma ," + metaData.get("name") + " " + i,
                        entry.getString("text"),
                        "Unexpected text in " + metaData.get(META_DATA_KEY_NAME) + " dd entry");
                }
                // also check directly in db for creation of dd
                if (metaData.get(META_DATA_KEY_NAME).equals("Asset")) {
                    assertTrue(assetDdRepository.findByName((String)metaData.get(META_DATA_KEY_DD_NAME)) != null);
                } else {
                    assertTrue(foDdRepository.findByName((String)metaData.get(META_DATA_KEY_DD_NAME)) != null);
                }

                // check for erroneous file and that no import done
                testAssetDdSb.append("bad record\n");
                String badDdName = metaData.get(META_DATA_KEY_DD_NAME) + "-BAD";

                try {
                    base64CompressedData = Base64.getEncoder().encodeToString(
                        compressionHelper.compress((badDdName + "\n" + testAssetDdSb.toString())));
                } catch (Exception e) {
                    fail(e);
                }
                response = apiManager.doApiRequest(base64CompressedData,
                    "http://localhost:" + port + url, HttpMethod.POST,
                                MediaType.APPLICATION_OCTET_STREAM, HttpStatus.BAD_REQUEST);
                ddResultAsJsonStr = response.getBody();
                // check dd not created for either data dictionaries
                assertTrue(assetDdRepository.findByName(badDdName) == null);
                assertTrue(foDdRepository.findByName(badDdName) == null);


            } catch (JSONException e) {
                e.printStackTrace();
                fail(e);
            }
        }


    }

    private void addIrToKey(Map<String, List<String>> irs, String rootIr, String key, int n) {
        irs.get(key).add(rootIr + "#" + n);
    }

    private void checkAssetOrFoResponse(String response, Map<String, List<String>> irs, String apiName) {
        int matched = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            assertEquals(irs.size(), jsonArray.length(), "Unexpected number of dd entries returned");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject ddeJson = jsonObject.getJSONObject("data_dictionary_entry");
                JSONArray irsJson = jsonObject.getJSONArray(apiName);
                List<String> rxedIrs = new ArrayList<>();
                for (int j = 0; j < irsJson.length(); j++) {
                    rxedIrs.add(irsJson.getString(j));
                }

                // match dd entry id to irs

                for (String key : irs.keySet()) {
                    if (key.equals(ddeJson.getString("entry_id"))) {
                        assertTrue(irs.get(key).size() == rxedIrs.size()
                            && irs.get(key).containsAll(rxedIrs)
                            && rxedIrs.containsAll(irs.get(key)));
                        matched++;
                    }
                }
            }
            assertEquals(jsonArray.length(), matched, "Unexpected number of ddes matched");
        } catch (JSONException e) {
            fail(e);
        }
    }


    private void addAndCheckIrs(Map<String, List<String>> irs, String apiName) {
        addAndCheckIrs(irs, apiName, HttpStatus.OK);
    }

    private void addAndCheckIrs(Map<String, List<String>> irs, String apiName, HttpStatus httpStatus) {
        StringBuilder testIrsSb = new StringBuilder();
        for (String key : irs.keySet()) {
            if (irs.get(key).size() == 0) {
                // treat empty set of irs as requirement for an invalid string
                testIrsSb.append(key).append("\n");
            } else {
                for (String ir : irs.get(key)) {
                    testIrsSb.append(key).append(",").append(ir).append("\n");
                }
            }
        }
        String url = "/api/" + apiName + "/import/pid/" + project1Id.toString();
        String base64CompressedData = null;
        try {
            base64CompressedData = Base64.getEncoder().encodeToString(compressionHelper.compress(testIrsSb.toString()));
        } catch (Exception e) {
            fail(e);
        }
        HttpEntity<String> response = apiManager.doApiRequest(base64CompressedData,
            "http://localhost:" + port + url, HttpMethod.POST, MediaType.APPLICATION_OCTET_STREAM, httpStatus);
        if (!httpStatus.equals(HttpStatus.OK)) {
            return;
        }
        this.checkAssetOrFoResponse(response.getBody(), irs, apiName);
    }

    @Test
    public void importFirs() {
        // use testProject1 to import some FIRs
        Map<String, List<String>> firs;
        // use std uniclass as these will not change(?)
        // keep local copy of what is set up for checking purposes
        firs = new HashMap<>();
        int firCount = 1;
        firs.put("EF_80", new ArrayList<>());
        firs.put("EF_80_10", new ArrayList<>());
        firs.put("EF_80_20", new ArrayList<>());
        firs.put("EF_80_50", new ArrayList<>());
        // put 2 firs in each fo
        for (String key : firs.keySet()) {
            addIrToKey(firs, "firs", key, firCount++);
            addIrToKey(firs, "firs", key, firCount++);
        }
        this.addAndCheckIrs(firs, "firs");
        // now change the firs and check updates as expected (not we can only add as it is checking an IMPORT function
        addIrToKey(firs, "firs", "EF_80_50", firCount++);
        addIrToKey(firs, "firs", "EF_80_50", firCount++);
        firs.put("EF_80_70", new ArrayList<>());
        addIrToKey(firs, "firs", "EF_80_70", firCount++);
        this.addAndCheckIrs(firs, "firs");

        // now try an invalid entry with some valid ones - check rejected and new ones not imported either
        // i.e. the api call is transactional
        // use Ss_20_30_75 as not used previously and easy to remove locally
        final String newKey = "EF_75";
        final String invalidKey = "Invalid-1";
        firs.put(newKey, new ArrayList<>());
        addIrToKey(firs, "firs", newKey, firCount++);
        firs.put(invalidKey, new ArrayList<>());
        addIrToKey(firs, "firs", invalidKey, firCount++);
        addIrToKey(firs, "firs", newKey, firCount++);
        this.addAndCheckIrs(firs, "firs", HttpStatus.BAD_REQUEST);
        firs.remove(newKey);
        firs.remove(invalidKey);
        // now check local and remote match
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/functional-outputs/pid/" + project1Id.toString());
        this.checkAssetOrFoResponse(response.getBody(), firs, "firs");
    }

    @Test
    public void importAirs() {
        // use testProject1 to import some AIRs
        Map<String, List<String>> airs;
        // use std uniclass as these should never change
        // keep local copy of what is set up for checking purposes
        airs = new HashMap<>();
        int airCount = 1;

        airs.put("Ss_20_20_75", new ArrayList<>());
        airs.put("Ss_20_20_75_35", new ArrayList<>());
        airs.put("Ss_20_20_75_45", new ArrayList<>());
        airs.put("Ss_20_20_75_65", new ArrayList<>());
        // put 2 airs in each fo
        for (String key : airs.keySet()) {
            addIrToKey(airs, "airs", key, airCount++);
            addIrToKey(airs, "airs", key, airCount++);
        }
        this.addAndCheckIrs(airs, "airs");
        // now change the airs and check updates as expected (note we can only add as it is checking an IMPORT function
        addIrToKey(airs, "airs", "Ss_20_20_75", airCount++);
        addIrToKey(airs, "airs", "Ss_20_20_75", airCount++);
        airs.put("Ss_20_20_75_70", new ArrayList<>());
        addIrToKey(airs, "airs", "Ss_20_20_75_70", airCount++);
        this.addAndCheckIrs(airs, "airs");

        // now try an invalid entry with some valid ones - check rejected and new ones not imported either
        // i.e. the api call is transactional
        // use Ss_20_30_75 as not used previously and easy to remove locally
        final String newKey = "Ss_20_30_75";
        final String invalidKey = "Invalid-1";
        airs.put(newKey, new ArrayList<>());
        addIrToKey(airs, "airs", newKey, airCount++);
        airs.put(invalidKey, new ArrayList<>());
        addIrToKey(airs, "airs", invalidKey, airCount++);
        addIrToKey(airs, "airs", newKey, airCount++);
        this.addAndCheckIrs(airs, "airs", HttpStatus.BAD_REQUEST);
        airs.remove(newKey);
        airs.remove(invalidKey);
        // now check local and remote match
        HttpEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/assets/pid/" + project1Id.toString());
        this.checkAssetOrFoResponse(response.getBody(), airs, "airs");
    }

    private void compressDecompress(String testString) {
        byte[] compressed = new byte[0];
        try {
            compressed = compressionHelper.compress(testString);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
        try {
            String decompressedString = compressionHelper.decompress(compressed);
            assertEquals(testString, decompressedString);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    public void compressionTest() {
        String testString = "This is a test string, to see if compression\nAnd decompression works!";
        compressDecompress(testString);
        compressDecompress(testString + "\n");
    }
}

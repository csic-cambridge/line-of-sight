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
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("no_security")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DataDictionaryRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestApiManager apiManager;

    @BeforeEach
    public void runBeforeAllTestMethods() {

    }

    private void fetchDataDictionaryList(String url, String expectedDdName, String expectedDdId) {
        try {
            final ResponseEntity<String> ddResponse = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + url);
            JSONArray jsonDdResponse = new JSONArray(ddResponse.getBody());
            // check one of the entries is for expectedDdId
            boolean uniclassFound = false;
            for (int i = 0; i < jsonDdResponse.length(); i++) {
                JSONObject dd = (JSONObject) jsonDdResponse.get(i);
                if (dd.getString("id").equals(expectedDdId)) {
                    uniclassFound = true;
                    assertAll(
                        () -> assertEquals(HttpStatus.OK, ddResponse.getStatusCode()),
                        () -> assertEquals(dd.get("name"), expectedDdName,
                            "Get data-dictionary for " + url + " does not have correct name")
                    );
                }
            }
            assertTrue(uniclassFound, "Expected data dictionary for " + url + " not found = " + expectedDdId);
        } catch (Exception e) {
            fail(e);
        }
    }

    private void fetchDataDictionaryEntries(String url, String ddIdFieldName, String ddId, int expectedEntryCount) {
        // now get data dictionary entries for uniclass dd
        try {
            final ResponseEntity<String> ddeResponse = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + url + "/" + ddId);
            JSONArray jsonDdResponse = new JSONArray(ddeResponse.getBody());
            assertEquals(jsonDdResponse.length(), expectedEntryCount, "Unexpected number of data dictionary entries");
            for (int i = 0; i < jsonDdResponse.length(); i++) {
                JSONObject dde = (JSONObject) jsonDdResponse.get(i);
                //assertEquals(dde.getString(ddIdFieldName), ddId, "Invalid dd id in dd entry");
            }
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void getFunctionalOutputDataDictionaries() throws Exception {
        // assumes the uniclass dictionary set up by liquibase is available
        // TODO ideally we should create a rival dictionary to make sure of separation
        String ddId = "97ee7a74-e8c7-11ec-8fea-0242ac120002";
        fetchDataDictionaryList("/api/functional-output-data-dictionary",
            "Uniclass2015_EF_v1_10", ddId);
        fetchDataDictionaryEntries("/api/functional-output-data-dictionary",
            "functional_output_dictionary_id", ddId, 107);
    }

    @Test
    public void getAssetOutputDataDictionaries() throws Exception {
        // assumes the uniclass dictionary set up by liquibase is available
        // TODO ideally we should create a rival dictionary to make sure of separation
        String ddId = "e1970a24-e8c7-11ec-8fea-0242ac120002";
        fetchDataDictionaryList("/api/asset-data-dictionary", "Uniclass2015_Ss_v1_25", ddId);
        fetchDataDictionaryEntries("/api/asset-data-dictionary", "dictionary_id", ddId, 2415);
    }
}

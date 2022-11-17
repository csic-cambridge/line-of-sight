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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;




@Component

public class TestDataDictionaryManager {
    private AssetDataDictionaryDAO assetDd;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    AssetDataDictionaryRepository assetDataDictionaryRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDataDictionaryEntryRepository;

    @Autowired
    FunctionalOutputDataDictionaryRepository foDataDictionaryRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository foDataDictionaryEntryRepository;

    public void fetchDataDictionaryList(String url, int port, String expectedDdName, UUID expectedDdId) {
        try {
            final ResponseEntity<String> ddResponse = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + url);
            JSONArray jsonDdResponse = new JSONArray(ddResponse.getBody());
            // check one of the entries is for expectedDdId
            boolean found = false;
            for (int i = 0; i < jsonDdResponse.length(); i++) {
                JSONObject dd = (JSONObject) jsonDdResponse.get(i);
                if (dd.getString("id").equals(expectedDdId.toString())) {
                    found = true;
                    assertAll(
                        () -> assertEquals(HttpStatus.OK, ddResponse.getStatusCode()),
                        () -> assertEquals(dd.get("name"), expectedDdName,
                            "Get data-dictionary for " + url + " does not have correct name")
                    );
                }
            }
            assertTrue(found, "Expected data dictionary for " + url + " not found = " + expectedDdId);
        } catch (Exception e) {
            fail(e);
        }
    }

    public JSONArray fetchDataDictionaryEntries(String url,
                                                int port,
                                                String ddIdFieldName,
                                                UUID ddId, int expectedEntryCount) {
        // now get data dictionary entries for dd
        try {
            final ResponseEntity<String> ddeResponse = apiManager.doSuccessfulGetApiRequest(
                "http://localhost:" + port + url + "/" + ddId.toString());
            JSONArray jsonDdResponse = new JSONArray(ddeResponse.getBody());
            assertEquals(expectedEntryCount, jsonDdResponse.length(),
                "Unexpected number of data dictionary entries");
            for (int i = 0; i < jsonDdResponse.length(); i++) {
                JSONObject dde = (JSONObject) jsonDdResponse.get(i);
                //assertEquals(dde.getString(ddIdFieldName), ddId, "Invalid dd id in dd entry");
            }
            return jsonDdResponse;
        } catch (Exception e) {
            fail(e);
        }
        return null;
    }

    @Transactional
    public void deleteAssetDataDictionary(UUID ddId) {
        assetDataDictionaryEntryRepository.deleteByAssetDictionaryId(ddId);
        assetDataDictionaryRepository.deleteById(ddId);
    }

    @Transactional
    public void deleteFoDataDictionary(UUID ddId) {
        foDataDictionaryEntryRepository.deleteByFoDictionaryId(ddId);
        foDataDictionaryRepository.deleteById(ddId);
    }
}

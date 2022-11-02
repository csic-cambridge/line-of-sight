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

import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;





@Component
public class TestAssetManager {
    private AssetDataDictionaryDAO assetDd;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    AssetDataDictionaryRepository assetDdRepository;

    @Autowired
    AssetDataDictionaryEntryRepository assetDdeRepository;

    public AssetDataDictionaryDAO getAssetDd() {
        return assetDd;
    }

    private void createAssetDictionary(String name) {
        this.assetDd = assetDdRepository.save(AssetDataDictionaryDAO.builder()
            .name(name + System.currentTimeMillis()).build());
    }

    public void deleteAssetDictionary(UUID assetDdId) {
        Set<AssetDataDictionaryEntryDAO> entries = assetDdeRepository.findByAssetDictionaryId(assetDdId);
        for (AssetDataDictionaryEntryDAO entry : entries) {
            assetDdeRepository.delete(entry);
        }
        assetDdRepository.deleteById(assetDdId);
    }


    public AssetDataDictionaryDAO createAssetDdWithTwoEntries() {
        String ddName = "Asset Dd";
        long curMs = System.currentTimeMillis();
        createAssetDictionary(ddName);
        for (int i = 0; i < 2; i++) {
            assetDdeRepository.save(AssetDataDictionaryEntryDAO.builder()
                .id("Entry #" + i + " id for " + ddName + curMs)
                .assetDictionaryId(this.assetDd.getId())
                .text("Entry #" + i + " text for " + ddName + curMs)
                .build());
        }
        return this.assetDd;
    }

    public AssetDAO createAsset(UUID projectId, int port, String firsRoot, AssetDataDictionaryEntryDAO assetDdEntry)
        throws JSONException {
        // create a new asset
        // List<AssetDataDictionaryEntryDAO>ddEntryDaos =
        //     new ArrayList<>(assetDdeRepository.findByAssetDictionaryId(this.assetDd.getId()));
        Set<String> sourceAirs = new HashSet<>();
        sourceAirs.add(firsRoot + "-1");
        sourceAirs.add(firsRoot + "-2");

        Map<String, Object> ddeMap = new HashMap<>();
        ddeMap.put("id", assetDdEntry.getId());
        ddeMap.put("text", "any text will do");
        Map<String, Object> map = new HashMap<>();
        map.put("data_dictionary_entry", ddeMap);
        map.put("airs", sourceAirs.toArray());
        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/assets/" + projectId);
        // build AssetDAO result from response
        String ddResultAsJsonStr = response.getBody();
        JSONObject assetJsonObject = new JSONObject(ddResultAsJsonStr);
        JSONObject ddeJsonObject = assetJsonObject.getJSONObject("data_dictionary_entry");
        AssetDataDictionaryEntryDAO assetDataDictionaryEntryDao = AssetDataDictionaryEntryDAO.builder()
            .id(ddeJsonObject.getString("id"))
            .assetDictionaryId(this.assetDd.getId())
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
            () -> assertEquals(assetDataDictionaryEntryDao.getId(), assetDdEntry.getId()),
            () -> assertEquals(assetDataDictionaryEntryDao.getText(), assetDdEntry.getId() + "-"
                + assetDdEntry.getText()),
            () -> assertEquals(assetDataDictionaryEntryDao.getAssetDictionaryId(), this.assetDd.getId()),
            () -> assertEquals(result.getAirs().size(), sourceAirs.size()),
            () -> assertTrue(result.getAirs().containsAll(sourceAirs)
                && sourceAirs.containsAll(result.getAirs()))
        );
        return result;
    }
}

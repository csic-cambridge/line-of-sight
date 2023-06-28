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

import com.costain.cdbb.model.Airs;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        Set<AssetDataDictionaryEntryDAO> entries = assetDdeRepository.findByAssetDictionaryIdOrderByEntryId(assetDdId);
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
                .entryId("Entry #" + i + " id for " + ddName + curMs)
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
        Set<Airs> sourceAirs = new HashSet<>();
        sourceAirs.add(new Airs().id(AirsDAO.NEW_ID).airs(firsRoot + "-1"));
        sourceAirs.add(new Airs().id(AirsDAO.NEW_ID).airs(firsRoot + "-2"));

        Map<String, Object> ddeMap = new HashMap<>();
        ddeMap.put("entry_id", assetDdEntry.getEntryId());
        ddeMap.put("text", "any text will do");
        Map<String, Object> map = new HashMap<>();
        map.put("data_dictionary_entry", ddeMap);
        map.put("airs", sourceAirs.toArray());
        String payload = null;
        try {
            payload = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            fail(e);
        }
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/assets/pid/" + projectId);
        // build AssetDAO result from response
        String ddResultAsJsonStr = response.getBody();
        JSONObject assetJsonObject = new JSONObject(ddResultAsJsonStr);
        JSONObject ddeJsonObject = assetJsonObject.getJSONObject("data_dictionary_entry");
        AssetDataDictionaryEntryDAO assetDataDictionaryEntryDao = AssetDataDictionaryEntryDAO.builder()
            .entryId(ddeJsonObject.getString("entry_id"))
            .assetDictionaryId(this.assetDd.getId())
            .text(ddeJsonObject.getString("text"))
            .build();
        JSONArray airsJsonArray = assetJsonObject.getJSONArray("airs");
        Set<AirsDAO> airs = new HashSet<>();
        for (int i = 0; i < airsJsonArray.length(); i++) {
            JSONObject jsonObjectAirs = (JSONObject)airsJsonArray.get(i);
            airs.add(AirsDAO.builder().airs(jsonObjectAirs.getString("airs"))
                .id(jsonObjectAirs.getString("id")).build());
        }
        AssetDAO result = AssetDAO.builder()
            .id(UUID.fromString(assetJsonObject.getString("id")))
            .dataDictionaryEntry(assetDataDictionaryEntryDao)
            .airs(airs)
            .build();
        assertAll(
            () -> assertEquals(assetDataDictionaryEntryDao.getEntryId(), assetDdEntry.getEntryId(),
                "Entry id incorrect"),
            () -> assertEquals(assetDataDictionaryEntryDao.getText(), assetDdEntry.getEntryId() + "-"
                + assetDdEntry.getText(), "Entry text incorrect"),
            () -> assertEquals(assetDataDictionaryEntryDao.getAssetDictionaryId(), this.assetDd.getId(),
                "Asset dd id incorrect"),
            () -> assertEquals(sourceAirs.size(), result.getAirs().size(),  "Airs size incorrect"),
            () -> assertTrue(compareAirsStrings(result.getAirs(), sourceAirs),
                "Result airs and source airs different")
        );
        return result;
    }

    public boolean compareAirsStrings(Set<AirsDAO> airs1, Set<Airs> airs2) {
        if (airs1 == null && airs2 == null) {
            return true;
        }
        if (airs1 == null || airs2 == null) {
            return false;
        }
        if (airs1.size() != airs2.size()) {
            return false;
        }
        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();
        for (AirsDAO airsDao : airs1) {
            strings1.add(airsDao.getAirs());
        }
        for (Airs airs : airs2) {
            strings2.add(airs.getAirs());
        }
        return strings1.containsAll(strings2) && strings2.containsAll(strings1);
    }

    public boolean compareAirsDaoStrings(Set<AirsDAO> airs1, Set<AirsDAO> airs2) {
        if (airs1 == null && airs2 == null) {
            return true;
        }
        if (airs1 == null || airs2 == null) {
            return false;
        }
        if (airs1.size() != airs2.size()) {
            return false;
        }
        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();
        for (AirsDAO airsDao : airs1) {
            strings1.add(airsDao.getAirs());
        }
        for (AirsDAO airs : airs2) {
            strings2.add(airs.getAirs());
        }
        return strings1.containsAll(strings2) && strings2.containsAll(strings1);
    }
}

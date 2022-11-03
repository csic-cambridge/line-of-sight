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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestFunctionalOutputManager {

    @Autowired
    TestApiManager apiManager;

    @Autowired
    FunctionalOutputDataDictionaryRepository functionalOutputDdRepository;

    @Autowired
    FunctionalOutputDataDictionaryEntryRepository functionalOutputDdeRepository;

    @Autowired
    FunctionalOutputRepository functionalOutputRepository;

    private FunctionalOutputDataDictionaryDAO functionalOutputDd;

    public FunctionalOutputDataDictionaryDAO getFunctionalOutputDd() {
        return functionalOutputDd;
    }

    private void createFunctionalOutputDictionary(String name) {
        this.functionalOutputDd = functionalOutputDdRepository.save(FunctionalOutputDataDictionaryDAO.builder()
            .name(name + System.currentTimeMillis()).build());
    }

    public void deleteFunctionalOutputDictionary(UUID id) {
        Set<FunctionalOutputDataDictionaryEntryDAO> entries = functionalOutputDdeRepository.findByFoDictionaryId(id);
        for (FunctionalOutputDataDictionaryEntryDAO entry : entries) {
            System.out.println("Deleting dd entry " + entry);
            functionalOutputDdeRepository.delete(entry);
        }
        functionalOutputDdRepository.deleteById(id);
    }

    private FunctionalOutputDataDictionaryEntryDAO createFunctionalOutputDictionaryEntryForDd(
        String entryId, String entryName, UUID dataDictionaryId) {
        return functionalOutputDdeRepository.save(FunctionalOutputDataDictionaryEntryDAO.builder()
            .id(entryId)
            .foDictionaryId(dataDictionaryId)
            .text(entryName)
            .build());
    }

    public FunctionalOutputDataDictionaryDAO createFoDdWithEntry(UUID projectId, int port) {
        String ddName = "Dd for create functionalOutput";
        String entry1Id = "Entry #1 id for " + ddName + System.currentTimeMillis();
        String entry1Text = "Entry #1 text for " + ddName + System.currentTimeMillis();

        // create a new functionalOutput data dictionary with 1 entry to refer functionalOutput to
        createFunctionalOutputDictionary(ddName);
        createFunctionalOutputDictionaryEntryForDd(entry1Id, entry1Text, this.functionalOutputDd.getId());

        return functionalOutputDd;
    }

    public FunctionalOutputDAO createFunctionalOutput(UUID projectId, int port) throws JSONException {
        List<FunctionalOutputDataDictionaryEntryDAO> ddEntryDaos =
            new ArrayList<>(functionalOutputDdeRepository.findByFoDictionaryId(this.functionalOutputDd.getId()));

        Set<String> sourceFirs = new HashSet<>();
        sourceFirs.add("FirTest1");
        sourceFirs.add("FirTest2");

        Map<String, Object> ddeMap = new HashMap<>();
        ddeMap.put("id", ddEntryDaos.get(0).getId());
        ddeMap.put("text", "any text");
        Map<String, Object> map = new HashMap<>();
        map.put("data_dictionary_entry", ddeMap);
        map.put("firs", sourceFirs.toArray());
        String payload = new GsonBuilder().disableHtmlEscaping().create().toJson(map);
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId);
        // build FunctionalOutputDAO result from response
        String ddResultAsJsonStr = response.getBody();
        JSONObject functionalOutputJsonObject = new JSONObject(ddResultAsJsonStr);
        JSONObject ddeJsonObject = functionalOutputJsonObject.getJSONObject("data_dictionary_entry");
        FunctionalOutputDataDictionaryEntryDAO functionalOutputDataDictionaryEntryDao =
            FunctionalOutputDataDictionaryEntryDAO.builder()
                .id(ddeJsonObject.getString("id"))
                .foDictionaryId(this.functionalOutputDd.getId())
                .text(ddeJsonObject.getString("text"))
                .build();
        JSONArray firsJsonArray = functionalOutputJsonObject.getJSONArray("firs");
        Set<String> firs = new HashSet<>();
        for (int i = 0; i < firsJsonArray.length(); i++) {
            firs.add((String)firsJsonArray.get(i));
        }
        FunctionalOutputDAO result = FunctionalOutputDAO.builder()
            .id(UUID.fromString(functionalOutputJsonObject.getString("id")))
            .dataDictionaryEntry(functionalOutputDataDictionaryEntryDao)
            .firs(firs)
            .build();
        assertAll(
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getId(), ddEntryDaos.get(0).getId()),
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getText(),
                ddEntryDaos.get(0).getId() + "-" + ddEntryDaos.get(0).getText()),
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getFoDictionaryId(),
                this.functionalOutputDd.getId()),
            () -> assertEquals(sourceFirs.size(), result.getFirs().size()),
            () -> assertTrue(result.getFirs().containsAll(sourceFirs)
                && sourceFirs.containsAll(result.getFirs()))
        );
        return result;
    }

    public void deleteFunctionalOutput(UUID projectId, int port, FunctionalOutputDAO functionalOutput) {
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId + "/" + functionalOutput.getId());
        // check it has been deleted from database
        assertFalse(functionalOutputRepository.findById(functionalOutput.getId()).isPresent());
    }
}

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
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.model.Airs;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.Firs;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
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
        Set<FunctionalOutputDataDictionaryEntryDAO> entries =
            functionalOutputDdeRepository.findByFoDictionaryIdOrderByEntryId(id);
        for (FunctionalOutputDataDictionaryEntryDAO entry : entries) {
            System.out.println("Deleting dd entry " + entry);
            functionalOutputDdeRepository.delete(entry);
        }
        functionalOutputDdRepository.deleteById(id);
    }

    private FunctionalOutputDataDictionaryEntryDAO createFunctionalOutputDictionaryEntryForDd(
        String entryId, String entryName, UUID dataDictionaryId) {
        return functionalOutputDdeRepository.save(FunctionalOutputDataDictionaryEntryDAO.builder()
            .entryId(entryId)
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
            new ArrayList<>(functionalOutputDdeRepository
                .findByFoDictionaryIdOrderByEntryId(this.functionalOutputDd.getId()));

        Set<Firs> sourceFirs = new HashSet<>();
        sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("FirTest1"));
        sourceFirs.add(new Firs().id(FirsDAO.NEW_ID).firs("FirTest2"));

        Map<String, Object> ddeMap = new HashMap<>();
        ddeMap.put("entry_id", ddEntryDaos.get(0).getEntryId());
        ddeMap.put("text", "any text");
        Map<String, Object> map = new HashMap<>();
        map.put("data_dictionary_entry", ddeMap);
        map.put("firs", sourceFirs.toArray());
        String payload = null;
        try {
            payload = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            fail(e);
        }
        ResponseEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            payload,
            "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId);
        // build FunctionalOutputDAO result from response
        String ddResultAsJsonStr = response.getBody();
        JSONObject functionalOutputJsonObject = new JSONObject(ddResultAsJsonStr);
        JSONObject ddeJsonObject = functionalOutputJsonObject.getJSONObject("data_dictionary_entry");
        FunctionalOutputDataDictionaryEntryDAO functionalOutputDataDictionaryEntryDao =
            FunctionalOutputDataDictionaryEntryDAO.builder()
                .entryId(ddeJsonObject.getString("entry_id"))
                .foDictionaryId(this.functionalOutputDd.getId())
                .text(ddeJsonObject.getString("text"))
                .build();
        JSONArray firsJsonArray = functionalOutputJsonObject.getJSONArray("firs");
        Set<FirsDAO> firs = new HashSet<>();
        for (int i = 0; i < firsJsonArray.length(); i++) {
            JSONObject jsonObjectFirs = (JSONObject)firsJsonArray.get(i);
            firs.add(FirsDAO.builder().id(jsonObjectFirs.getString("id"))
                    .firs(jsonObjectFirs.getString("firs")).build());
        }
        FunctionalOutputDAO result = FunctionalOutputDAO.builder()
            .id(UUID.fromString(functionalOutputJsonObject.getString("id")))
            .dataDictionaryEntry(functionalOutputDataDictionaryEntryDao)
            .firs(firs)
            .build();
        assertAll(
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getEntryId(), ddEntryDaos.get(0).getEntryId(),
                "Dd entry ids differ"),
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getText(),
                ddEntryDaos.get(0).getEntryId() + "-" + ddEntryDaos.get(0).getText(),
                "Entry text incorrect"),
            () -> assertEquals(functionalOutputDataDictionaryEntryDao.getFoDictionaryId(),
                this.functionalOutputDd.getId(), "FO dd id incorrect"),
            () -> assertEquals(sourceFirs.size(), result.getFirs().size(), "Firs size incorrect"),
            () -> assertTrue(compareFirsStrings(result.getFirs(), sourceFirs))
        );
        return result;
    }

    public void deleteFunctionalOutput(UUID projectId, int port, FunctionalOutputDAO functionalOutput) {
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/functional-outputs/pid/" + projectId + "/" + functionalOutput.getId());
        // check it has been deleted from database
        assertFalse(functionalOutputRepository.findById(functionalOutput.getId()).isPresent());
    }

    public boolean compareFirsStrings(Set<FirsDAO> firs1, Set<Firs> firs2) {
        if (firs1 == null && firs2 == null) {
            return true;
        }
        if (firs1 == null || firs2 == null) {
            return false;
        }
        if (firs1.size() != firs2.size()) {
            return false;
        }
        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();
        for (FirsDAO firsDao : firs1) {
            strings1.add(firsDao.getFirs());
        }
        for (Firs firs : firs2) {
            strings2.add(firs.getFirs());
        }
        return strings1.containsAll(strings2) && strings2.containsAll(strings1);
    }

    public boolean compareFirsDaos(Set<FirsDAO> firs1, Set<FirsDAO> firs2) {
        if (firs1 == null && firs2 == null) {
            return true;
        }
        if (firs1 == null || firs2 == null) {
            return false;
        }
        if (firs1.size() != firs2.size()) {
            return false;
        }
        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();
        for (FirsDAO firsDao : firs1) {
            strings1.add(firsDao.getFirs());
        }
        for (FirsDAO firs : firs2) {
            strings2.add(firs.getFirs());
        }
        return strings1.containsAll(strings2) && strings2.containsAll(strings1);
    }
}

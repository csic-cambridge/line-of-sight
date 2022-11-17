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


import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestDataDictionaryManager;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;




@ActiveProfiles("no_security")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DataDictionaryRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestApiManager apiManager;

    @Autowired
    TestDataDictionaryManager dataDictionaryManager;

    @BeforeEach
    public void runBeforeAllTestMethods() {

    }

    @Test
    public void getFunctionalOutputDataDictionaries() throws Exception {
        // assumes the uniclass dictionary set up by liquibase is available
        // TODO ideally we should create a rival dictionary to make sure of separation
        dataDictionaryManager.fetchDataDictionaryList("/api/functional-output-data-dictionary", port,
            "Uniclass2015_EF_v1_10", TestApiManager.sampleFoDdId);
        dataDictionaryManager.fetchDataDictionaryEntries("/api/functional-output-data-dictionary", port,
            "functional_output_dictionary_id", TestApiManager.sampleFoDdId, 107);
    }

    @Test
    public void getAssetOutputDataDictionaries() throws Exception {
        // assumes the uniclass dictionary set up by liquibase is available
        // TODO ideally we should create a rival dictionary to make sure of separation
        dataDictionaryManager.fetchDataDictionaryList(
            "/api/asset-data-dictionary", port, "Uniclass2015_Ss_v1_25", TestApiManager.sampleAssetDdId);
        dataDictionaryManager.fetchDataDictionaryEntries(
            "/api/asset-data-dictionary", port,"dictionary_id", TestApiManager.sampleAssetDdId, 2415);
    }
}

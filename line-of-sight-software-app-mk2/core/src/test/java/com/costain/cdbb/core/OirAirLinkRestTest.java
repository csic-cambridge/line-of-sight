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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.costain.cdbb.core.config.Oauth2Provider;
import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestOoManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.OirDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.repositories.AirsRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.OirRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;






@ActiveProfiles("no_security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OirAirLinkRestTest {
    @LocalServerPort
    private int port;
    private static UUID project1Id;

    private static int existingUserCount = 0;
    private static List<UserDAO> testUsers;

    UUID copiedProjectId = null;

    @Autowired
    Oauth2Provider provider;

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    private TestOoManager ooManager;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OirRepository oirRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AirsRepository airsRepository;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            project1Id = projectManager.create("OIR / AIR Link Test", port);
        } catch (Exception e) {
            fail("Failed to create initial project(s)" + e);
        }
    }

    @AfterAll
    public void runAfterTestsFinish() {
        try {
            projectManager.delete(project1Id, port);
            projectManager.delete(copiedProjectId, port);
        } catch (Exception e) {
            fail("Failed to delete initial project" + e);
        }
    }

    @Test
    public void createAndRemoveOirAndAirLinks() {
        try {
            // a) AIR#6 is not linked via any entities - no link possible
            // b) OIR#2 is linked via entities to AIR#2 but not directly linked - link possible

            copiedProjectId = projectManager.createCopyOfSampleProject(port);
            List<ProjectOrganisationalObjectiveDAO> poosFromDb =
                pooRepository.findByProjectIdAndOoVersionOoIsDeletedFalse(copiedProjectId);
            Set<AssetDAO> projectAssetsFromDb = assetRepository.findByProjectId(copiedProjectId);
            Collection<OirDAO> oirDaos = poosFromDb.get(0).getOoVersion().getOo().getOirDaos();
            // a) find asset with AIR#6
            // find any OIR as none linked through entities to AIR#6
            Optional<OirDAO> oirDao = oirDaos.stream().findFirst();
            assertTrue(oirDao.isPresent(), "No Oir found to link");
            OirDAO linkOirsDao = oirDao.get();
            // find AIR#6
            AirsDAO linkAirDao = findAirDao(projectAssetsFromDb, "AIR #6");
            assertTrue(linkAirDao != null, "Failed to find 'AIR #6' for attempted linking");

            ResponseEntity<String> response =
                ooManager.linkOirWithAir(copiedProjectId, linkOirsDao.getId(), linkAirDao.getId(),
                    TestOoManager.LINK_OIR_AIR, port, HttpStatus.BAD_REQUEST);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            assertTrue(jsonResponse.has("error")
                && jsonResponse.getString("error")
                .equals("oirId and airId must be linked through FR, FO and Asset entities"));

            // b) OIR#2 is linked via entities to AIR#2 but not directly linked
            linkOirsDao = findOirDao(poosFromDb, "OIR #2");
            assertTrue(linkOirsDao != null, "Failed to find 'OIR #2' for attempted linking");
            linkAirDao = findAirDao(projectAssetsFromDb, "AIR #2");
            assertTrue(linkAirDao != null, "Failed to find 'AIR #2' for attempted linking");
            // link...
            response =
                ooManager.linkOirWithAir(copiedProjectId, linkOirsDao.getId(), linkAirDao.getId(),
                    TestOoManager.LINK_OIR_AIR, port, HttpStatus.OK);
            // link again - idempotent action
            response =
                ooManager.linkOirWithAir(copiedProjectId, linkOirsDao.getId(), linkAirDao.getId(),
                    TestOoManager.LINK_OIR_AIR, port, HttpStatus.OK);
            // and unlink...
            response =
                ooManager.linkOirWithAir(copiedProjectId, linkOirsDao.getId(), linkAirDao.getId(),
                    TestOoManager.UNLINK_OIR_AIR, port, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in call to createCopyOfSampleProject" + e);
        }
    }

    private AirsDAO findAirDao(Set<AssetDAO> projectAssetsFromDb, String air) {
        for (AssetDAO assetDao : projectAssetsFromDb) {
            if (assetDao.getAirs().size() > 0) {
                for (AirsDAO airsDao : assetDao.getAirs()) {
                    if (airsDao.getAirs().equals(air)) {
                        return airsDao;
                    }
                }
            }
        }
        return null;
    }

    private OirDAO findOirDao(List<ProjectOrganisationalObjectiveDAO> poosFromDb, String oir) {
        for (ProjectOrganisationalObjectiveDAO pooDao : poosFromDb) {
            for (OirDAO oirDao : pooDao.getOoVersion().getOo().getOirDaos()) {
                if (oirDao.getOirs().equals(oir)) {
                    return oirDao;
                }
            }
        }
        return null;
    }
}

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
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;


// This class manages projects for the integration tests
@Component
public class TestProjectManager {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    OrganisationalObjectiveRepository ooRepository;

    @Autowired
    FunctionalRequirementRepository frRepository;

    @Autowired
    TestApiManager apiManager;

    public UUID create(String projectName, int port)
        throws Exception {
        System.out.println("Starting test create");
        JSONObject projectJsonObject;
        projectJsonObject = new JSONObject();
        projectJsonObject.put("name", projectName + " test @" + System.currentTimeMillis());
        projectJsonObject.put("fo_dd_id","97ee7a74-e8c7-11ec-8fea-0242ac120002");
        projectJsonObject.put("asset_dd_id", "e1970a24-e8c7-11ec-8fea-0242ac120002");

        HttpEntity<String> response = apiManager.doSuccessfulPostApiRequest(
            projectJsonObject.toString(),
            "http://localhost:" + port + "/api/project");
        String projectResultAsJsonStr = response.getBody();

        JSONObject jsonObject = new JSONObject(projectResultAsJsonStr);
        assertTrue(jsonObject.has("id"), "Create project response has no id field");
        UUID projectId = UUID.fromString((String) jsonObject.get("id"));
        Set<OrganisationalObjectiveDAO> ooDaos = ooRepository.findByIsDeleted(false);
        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
        assertAll(
            () -> assertTrue((ooDaos.size() == pooDaos.size()) && (ooDaos.size() > 0),
                "Expected " + ooDaos.size() + " poos but found " + pooDaos.size()),
            () -> assertTrue(jsonObject.has("name"), "Create project response has no name field")
        );
        System.out.println("Project " + projectId + " is created");
        return projectId;
    }

    public void delete(UUID projectId, int port) {
        //Delete the created project
        System.out.println("Starting test deleteProjectFn");
        Optional<ProjectDAO> optProject = projectId != null ? projectRepository.findById(projectId) : Optional.empty();
        if (optProject.isEmpty()) {
            System.out.println("Project " + projectId + " not found for deletion");
            return;
        }

        //TODO Using api,  add some functional requirements, [ to do : fos, firs, assets and airs]

        // now delete it
        apiManager.doSuccessfulDeleteApiRequest(
            "http://localhost:" + port + "/api/project/" + projectId);

        // ensure all data for project is deleted

        List<ProjectOrganisationalObjectiveDAO> pooDaos =
            pooRepository.findByProjectIdOrderByOoVersionNameAsc(projectId);
        Set<FunctionalRequirementDAO> frDaos = frRepository.findByProjectIdOrderByNameAsc(projectId);
        assertAll(
            () -> assertTrue(pooDaos.isEmpty(), "Poos found when should have been deleted"),
            () -> assertTrue(frDaos.isEmpty(), "Functional requirements found when should have been deleted")
        );
        System.out.println("Project " + projectId + " is deleted");
    }

}

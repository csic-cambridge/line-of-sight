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

import com.costain.cdbb.core.config.Oauth2Provider;
import com.costain.cdbb.core.helpers.AuthorityTestData;
import com.costain.cdbb.core.helpers.TestApiManager;
import com.costain.cdbb.core.helpers.TestAuthoritiesManager;
import com.costain.cdbb.core.helpers.TestProjectManager;
import com.costain.cdbb.core.permissions.ProjectPermissionId;
import com.costain.cdbb.core.permissions.ProjectPermissionTypes;
import com.costain.cdbb.core.permissions.UserPermissionId;
import com.costain.cdbb.core.permissions.UserPermissionTypes;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.model.UserPermissionDAO;
import com.costain.cdbb.model.UserProjectPermissionDAO;
import com.costain.cdbb.model.helpers.AuthoritiesHelper;
import com.costain.cdbb.model.helpers.LoginHelper;
import com.costain.cdbb.repositories.UserPermissionRepository;
import com.costain.cdbb.repositories.UserProjectPermissionRepository;
import com.costain.cdbb.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;



@ActiveProfiles("no_security")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestTest {
    @LocalServerPort
    private int port;
    private static UUID project1Id;
    private static UUID project2Id;

    private static int existingUserCount = 0;
    private static List<UserDAO> testUsers;

    @Autowired
    Oauth2Provider provider;

    @Autowired
    private TestApiManager apiManager;

    @Autowired
    private TestProjectManager projectManager;

    @Autowired
    private TestAuthoritiesManager authoritiesManager;

    @Autowired
    AuthoritiesHelper authoritiesHelper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProjectPermissionRepository userProjectPermissionRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Autowired
    LoginHelper oauthProviders;

    @BeforeAll
    public void runBeforeTestsBegin() {
        try {
            project1Id = projectManager.create("Project User Permissions Test1", port);
            project2Id = projectManager.create("Project User Permissions Test2", port);
        } catch (Exception e) {
            fail("Failed to create initial project(s)" + e);
        }
        createTestUsers();
    }

    @AfterAll
    public void runAfterTestsFinish() {
        try {
            projectManager.delete(project1Id, port);
            projectManager.delete(project2Id, port);
        } catch (Exception e) {
            fail("Failed to delete initial project(s)" + e);
        }
        deleteTestUsers();
    }

    private void createTestUsers() {
        existingUserCount = ((Long)userRepository.count()).intValue();

        testUsers = new ArrayList<>();

        testUsers.add(UserDAO.builder().emailAddress("SuperUser1@example.com").isSuperUser(true).build());
        testUsers.add(UserDAO.builder().emailAddress("User1@example.com").isSuperUser(false).build());
        testUsers.add(UserDAO.builder().emailAddress("User2@example.com").isSuperUser(false).build());
        testUsers.forEach(userDao -> userRepository.save(userDao));
    }

    private void deleteTestUsers() {
        testUsers.forEach(userDao -> userRepository.delete(userDao));
    }

    private void checkUsers(List<UserDAO> users) {
        assertEquals(testUsers.size() + existingUserCount, users.size(), "Unexpected number of users");
        AtomicInteger matchCount = new AtomicInteger();
        users.forEach(userDao -> {
            testUsers.forEach(testDao -> {
                if (testDao.getUserId().equals(userDao.getUserId())) {
                    assertEquals(testDao, userDao, "UserDaos do not match");
                    matchCount.getAndIncrement();
                }
            });
        });
        assertEquals(testUsers.size(), matchCount.get(), "Did not match all testUsers");
    }

    private void fetchAndCheckUsers() {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/user");
        String userResultAsJsonStr = response.getBody();
        try {
            JSONArray jsonArray = new JSONArray(userResultAsJsonStr);
            assertEquals(testUsers.size() + existingUserCount, jsonArray.length(),
                "Unexpected number of users");
            List<UserDAO> fetchedUsers = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                fetchedUsers.add(UserDAO.builder()
                    .userId(UUID.fromString(jsonObject.getString("user_id")))
                    .emailAddress(jsonObject.getString("email_address"))
                    .isSuperUser(jsonObject.getBoolean("is_super_user"))
                    .build());
            }
            checkUsers(fetchedUsers);
        } catch (Exception e) {
            fail(e);
        }
    }

    private void checkProjectPermissionsAgainstDb(UUID userId, UUID projectId, JSONArray jsonArrayResponse)
        throws JSONException {

        List<UserProjectPermissionDAO> permissions =
            userProjectPermissionRepository.findById_UserIdAndId_ProjectId(userId, projectId);
        int origNumberOfPermissions = permissions.size();
        // need to match permissions against jsonArrayResponse permissions exactly
        assertEquals(ProjectPermissionTypes.projectPermissionTypes.size(), jsonArrayResponse.length(),
            "Incorrect number of permissions for user/project");
        // permissions will only contain Daos for permissions granted
        int grantedMatchCount = 0;
        for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject jsonObjectPermission = jsonArrayResponse.getJSONObject(i);
            int id = jsonObjectPermission.getInt("id");
            String name = jsonObjectPermission.getString("name");
            boolean isGranted = jsonObjectPermission.getBoolean("is_granted");
            if (isGranted) {
                // find this permission in db fetch and remove
                grantedMatchCount++;
                permissions.removeIf(permissionsDao ->
                    permissionsDao.getId().getPermissionId() == id
                        && ProjectPermissionTypes.getPermissionNameForId(
                            new ProjectPermissionId(permissionsDao.getId().getPermissionId())).equals(name)
                        && isGranted
                );
                assertEquals(origNumberOfPermissions - grantedMatchCount, permissions.size(),
                    "Failed to match a granted permission = " + id + ":" + name);
            }
        }
    }

    private void checkUserPermissionsAgainstDb(UUID userId, JSONArray jsonArrayResponse)
        throws JSONException {
        List<UserPermissionDAO> permissions =
            userPermissionRepository.findById_UserId(userId);
        int origNumberOfPermissions = permissions.size();
        // need to match permissions against jsonArrayResponse permissions exactly
        assertEquals(UserPermissionTypes.userPermissionTypes.size(), jsonArrayResponse.length(),
            "Incorrect number of permissions for user");
        // permissions will only contain Daos for permissions granted
        int grantedMatchCount = 0;
        for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject jsonObjectPermission = jsonArrayResponse.getJSONObject(i);
            int id = jsonObjectPermission.getInt("id");
            String name = jsonObjectPermission.getString("name");
            boolean isGranted = jsonObjectPermission.getBoolean("is_granted");
            if (isGranted) {
                // find this permission in db fetch and remove
                grantedMatchCount++;
                permissions.removeIf(permissionsDao ->
                    permissionsDao.getId().getPermissionId() == id
                        && UserPermissionTypes.getPermissionNameForId(
                            new UserPermissionId(permissionsDao.getId().getPermissionId())).equals(name)
                        && isGranted
                );
                assertEquals(origNumberOfPermissions - grantedMatchCount, permissions.size(),
                    "Failed to match a granted permission = " + id + ":" + name);
            }
        }
    }

    private Map<String, Object> createUserPermissionMap(UserPermissionId permissionId, boolean grant) {
        Map<String, Object> permissionMap = new HashMap<>();
        permissionMap.put("id", permissionId.getId());
        // no need for 'name', id is sufficient
        permissionMap.put("is_granted", grant);
        return permissionMap;
    }

    private Map<String, Object> createProjectPermissionMap(ProjectPermissionId permissionId, boolean grant) {
        Map<String, Object> permissionMap = new HashMap<>();
        permissionMap.put("id", permissionId.getId());
        // no need for 'name', id is sufficient
        permissionMap.put("is_granted", grant);
        return permissionMap;
    }

    private void fetchAndCheckPermissionsForUserAndProject(UserDAO user, UUID projectId) {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/project-permissions/"
                + user.getUserId().toString() + "/pid/"
                + projectId.toString());

        try {
            String permissionsResultAsJsonStr = response.getBody();

            JSONObject jsonObject = new JSONObject(permissionsResultAsJsonStr);
            assertEquals(user.getUserId().toString(), jsonObject.getString("user_id"), "User id incorrect");
            assertEquals(projectId.toString(), jsonObject.getString("project_id"), "Project id incorrect");
            JSONArray jsonArray = jsonObject.getJSONArray("permissions");
            this.checkProjectPermissionsAgainstDb(user.getUserId(), projectId, jsonArray);
        } catch (Exception e) {
            fail(e);
        }
    }

    private void fetchAndCheckPermissionsForUser(UserDAO user) {
        ResponseEntity<String> response = apiManager.doSuccessfulGetApiRequest(
            "http://localhost:" + port + "/api/user-permissions/"
                + user.getUserId().toString());

        try {
            String permissionsResultAsJsonStr = response.getBody();

            JSONObject jsonObject = new JSONObject(permissionsResultAsJsonStr);
            assertEquals(user.getUserId().toString(), jsonObject.getString("user_id"), "User id incorrect");
            JSONArray jsonArray = jsonObject.getJSONArray("permissions");
            this.checkUserPermissionsAgainstDb(user.getUserId(), jsonArray);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void fetchUsersAndExerciseProjectPermissions() {
        fetchAndCheckUsers();

        // check initial project permissions for each project created in this test and then modify and check updates
        for (UUID projectId : new UUID[]{project1Id, project2Id}) {
            testUsers.forEach(user -> {
                for (boolean isGranted : new boolean []{true, false, true, false}) {
                    fetchAndCheckPermissionsForUserAndProject(user, projectId);
                    // modify some permissions

                    ArrayList<Map<String, Object>> permissions = new ArrayList<>();
                    int count = 0;
                    boolean granted = isGranted;
                    for (Integer id : ProjectPermissionTypes.projectPermissionTypes.keySet()) {
                        if (count < 4) {
                            permissions.add(this.createProjectPermissionMap(new ProjectPermissionId(id), granted));
                            granted = !granted;
                        }
                        count++;
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("user_id", user.getUserId().toString());
                    map.put("projectId", projectId.toString());
                    map.put("permissions", permissions);
                    String payload = null;
                    try {
                        payload = new ObjectMapper().writeValueAsString(map);
                    } catch (JsonProcessingException e) {
                        fail(e);
                    }
                    ResponseEntity<String> permissionResponse = apiManager.doSuccessfulPutApiRequest(
                        payload,
                        "http://localhost:" + port + "/api/project-permissions/"
                            + user.getUserId().toString() + "/pid/"
                            + projectId.toString());
                    // check database permissions updated as required
                    List<UserProjectPermissionDAO> uppDaos =
                        userProjectPermissionRepository.findById_UserIdAndId_ProjectId(user.getUserId(), projectId);
                    assertEquals(2, uppDaos.size(), "Unexpected number of project permissions in database");
                    fetchAndCheckPermissionsForUserAndProject(user, projectId);
                }
            });
        }
    }

    @Test
    public void fetchUsersAndExerciseUserPermissions() {
        fetchAndCheckUsers();

        // exercise change of super user status
        testUsers.forEach(user -> {
            user.setIsSuperUser(!user.isSuperUser());
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", "");
            map.put("is_super_user", user.isSuperUser());
            String payload = null;
            try {
                payload = new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                fail(e);
            }
            ResponseEntity<String>  userResponse = apiManager.doSuccessfulPutApiRequest(
                payload,
                "http://localhost:" + port + "/api/user/"
                    + user.getUserId().toString());
            try {
                JSONObject jsonObject = new JSONObject(userResponse.getBody());
                assertEquals(user.getUserId().toString(), jsonObject.getString("user_id"),
                    "User ids do no match");
                assertEquals(user.getEmailAddress(), jsonObject.getString("email_address"),
                    "User email addresses do no match");
                assertEquals(user.isSuperUser(), jsonObject.getBoolean("is_super_user"),
                    "User super user statuses do no match");

            } catch (Exception e) {
                fail(e);
            }
            fetchAndCheckUsers();
        });


        // check initial user permissions and then modify and check updates
        // Given there is only 1 user permission all we can test is granting and removing that permission
        testUsers.forEach(user -> {
            for (boolean isGranted : new boolean []{true, false, true, false}) {
                fetchAndCheckPermissionsForUser(user);
                // modify some permissions

                ArrayList<Map<String, Object>> permissions = new ArrayList<>();
                int count = 0;
                boolean granted = isGranted;
                for (Integer id : UserPermissionTypes.userPermissionTypes.keySet()) {
                    if (count < 4) {
                        permissions.add(this.createUserPermissionMap(new UserPermissionId(id), granted));
                        granted = !granted;
                    }
                    count++;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("user_id", user.getUserId().toString());
                map.put("permissions", permissions);
                String payload = null;
                try {
                    payload = new ObjectMapper().writeValueAsString(map);
                } catch (JsonProcessingException e) {
                    fail(e);
                }
                ResponseEntity<String> permissionResponse = apiManager.doSuccessfulPutApiRequest(
                    payload,
                    "http://localhost:" + port + "/api/user-permissions/"
                        + user.getUserId().toString());
                // check database permissions updated as required

                List<UserPermissionDAO> upDaos =
                    userPermissionRepository.findById_UserId(user.getUserId());
                assertEquals(2, upDaos.size(), "Unexpected number of user permissions in database");
                fetchAndCheckPermissionsForUser(user);
            }
        });
    }

    @Test
    public void authoritiesTest() {
        // check authorities required for urls and methods
        List<AuthorityTestData> testData = authoritiesManager.setData(testUsers.get(0), project1Id);
        testData.forEach(data -> {
            if (data.getUrl().startsWith("/api/project/export")
            ) {
                System.out.println("check"); // to help with debugging
            }
            List<String> requiredAuthorities =
                authoritiesHelper.getAuthoritiesRequiredForUrlAndMethod(data.getUrl(), data.getMethod());
            assertTrue(requiredAuthorities != null,
                "Failed to find requiredAuthorities for " + data.getUrl() + data.getMethod().toString());
            assertTrue(requiredAuthorities.containsAll(data.getExpectedAuthorities())
                    && data.getExpectedAuthorities().containsAll(requiredAuthorities),
                "Expected authorities do not match required list for " + data.getUrl() + data.getMethod().toString()
                    + ", expected " + data.getExpectedAuthorities().toString()
                    + " but got " + requiredAuthorities.toString());
        });
    }

    @Test
    public void loginTest() {
        ResponseEntity<String>  loginResponse = apiManager.doSuccessfulPostApiRequest(
            null,
            "http://localhost:" + port + "/api/oauth-providers");
        try {
            JSONArray jsonArray = new JSONArray(loginResponse.getBody());
            assertEquals(2, jsonArray.length(), "Unexpected provider count returned");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String provider = jsonObject.getString("name");
                String url = jsonObject.getString("url");
                System.out.println("Login provider = " + provider + ", url = " + url);
            }
        } catch (Exception e) {
            fail(e);
        }
    }
}

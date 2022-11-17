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

import com.costain.cdbb.core.permissions.ProjectPermissionTypes;
import com.costain.cdbb.core.permissions.UserPermissionTypes;
import com.costain.cdbb.model.UserDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;





// Creates the data used to test all the api permissions/authority tests
// Actual tests in UserRestTest.java

@Component
public class TestAuthoritiesManager {
    private UserDAO testUser;
    private UUID projectId;

    public List<AuthorityTestData> setData(UserDAO testUser, UUID projectId) {
        this.testUser = testUser;
        this.projectId = projectId;
        return createAuthoritiesTestData();
    }

    private List<AuthorityTestData> createAuthoritiesTestData() {
        // need to test every api/http method defined in cdbb-api.yaml

        List<AuthorityTestData> testData = new ArrayList<>();
        // try to do this in same order as cdbb-api.yaml file
        this.addOoApiCallsData(testData);
        this.addFrApiCallsData(testData);
        this.addFoApiCallsData(testData);
        this.addAssetApiCallsData(testData);
        this.addDataDictionaryApiCallsData(testData);
        this.addProjectApiCallsData(testData);
        this.addPermissionsApiCallsData(testData);
        return testData;
    }

    private void addOoApiCallsData(List<AuthorityTestData> testData) {
        //organisational-objectives
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/organisational-objectives",

                UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_VIEW_OO_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/organisational-objectives",

                UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)
            ));

        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.PUT, "/api/organisational-objectives/{id}",

                UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.DELETE, "/api/organisational-objectives/{id}",

                UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_OO_OIR_ID)
            ));

        // POO
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/project-organisational-objectives/pid/{projectid}",

                ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/project-organisational-objectives/pid/{projectid}/{pooId}}",

                ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_POO_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.DELETE, "/api/project-organisational-objectives/pid/{projectid}/{pooid}}",

                ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_POO_ID)
            ));
    }

    private void addFrApiCallsData(List<AuthorityTestData> testData) {
        // FR
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/functional-requirements/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.POST, "/api/functional-requirements/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FR_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/functional-requirements/pid/{projectid}/{foId}}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FR_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.DELETE, "/api/functional-requirements/pid/{projectid}/{foId}}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_FR_ID)
            ));
    }

    private void addFoApiCallsData(List<AuthorityTestData> testData) {
        // FO
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/functional-outputs/pid/{projectid}",

                ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.POST, "/api/functional-outputs/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FO_ID)
            ));

        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/functional-outputs/pid/{projectid}/{foid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_EDIT_FO_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.DELETE, "/api/functional-outputs/pid/{projectid}/{foid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_FO_ID)
            ));
        // import FIRs
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/firs/import/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                ProjectPermissionTypes.PROJECT_PERMISSION_IMPORT_DATA_ID)
        ));
    }

    private void addAssetApiCallsData(List<AuthorityTestData> testData) {
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/assets/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_VIEW_PROJECT_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.POST, "/api/assets/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_ADD_ASSET_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/assets/pid/{projectid}/{assetid}}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_ADD_ASSET_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.DELETE, "/api/assets/pid/{projectid}/{assetid}}",
            ProjectPermissionTypes.getAuthorityNameForId(
                    ProjectPermissionTypes.PROJECT_PERMISSION_DELETE_ASSET_ID)
            ));
        // import airs
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/airs/import/pid/{projectid}",
            ProjectPermissionTypes.getAuthorityNameForId(
                ProjectPermissionTypes.PROJECT_PERMISSION_IMPORT_DATA_ID)
        ));
    }

    private void addDataDictionaryApiCallsData(List<AuthorityTestData> testData) {
        // fo data dictionary
        // asset data dictionary
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/asset-data-dictionary",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)
        ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/asset-data-dictionary/{id}",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)));
        // asset data dictionary
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/functional-output-data-dictionary",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/functional-output-data-dictionary/{id}",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)));
        // imports
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/functional-output-data-dictionary",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_IMPORT_ID)
        ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/asset-data-dictionary",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_IMPORT_ID)
        ));
    }

    private void addProjectApiCallsData(List<AuthorityTestData> testData) {
        // Project API - no project id
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/project",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_VIEW_PROJECT_DASHBOARD_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/project",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)
            ));

        // Project API - with project id
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.POST, "/api/project/pid/{projectid}",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/project/pid/{projectid}",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_EDIT_PROJECTS_ID)
            ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.DELETE, "/api/project/pid/{projectid}",
            UserPermissionTypes.getAuthorityNameForId(
                    UserPermissionTypes.USER_PERMISSION_DELETE_PROJECTS_ID)
            ));
        // project import
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.POST, "/api/project/import",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_IMPORT_ID)
        ));
        // project export
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/project/export/pid/{projectid}",
            UserPermissionTypes.getAuthorityNameForId(
                UserPermissionTypes.USER_PERMISSION_EXPORT_ID)
        ));

    }

    private void addPermissionsApiCallsData(List<AuthorityTestData> testData) {
        // user permissions - all super user except GET which has user as well
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.GET, "/api/user-permissions/{userid}",
            UserPermissionTypes.ROLE_USER // user and super user
        ));
        testData.add(new AuthorityTestData(this.testUser,
            null,
            HttpMethod.PUT, "/api/user-permissions/{userid}",
            null // just super user
        ));

        //project permissions - all super user except GET which has user as well
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.GET, "/api/project-permissions/{userid}/pid/{projectid}",
            UserPermissionTypes.ROLE_USER // user and super user
        ));
        testData.add(new AuthorityTestData(this.testUser,
            this.projectId,
            HttpMethod.PUT, "/api/project-permissions/{userid}/pid/{projectid}",
            null // just super user
        ));
    }
}

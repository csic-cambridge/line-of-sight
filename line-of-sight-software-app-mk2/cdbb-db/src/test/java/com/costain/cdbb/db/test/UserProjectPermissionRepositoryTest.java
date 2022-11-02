package com.costain.cdbb.db.test;


import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.PermissionDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.model.UserProjectPermissionDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryRepository;
import com.costain.cdbb.repositories.PermissionRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import com.costain.cdbb.repositories.UserProjectPermissionRepository;
import com.costain.cdbb.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;


@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserProjectPermissionRepositoryTest {
    private final Logger logger = LoggerFactory.getLogger(UserProjectPermissionRepositoryTest.class);

    private final static String TEST_PROJECT_NAME_1 = "Test Project 1";
    private final static String TEST_PROJECT_NAME_2 = "Test Project 2";
    private final static String TEST_ASSET_DICTIONARY_NAME = "Test Asset Dictionary";
    private final static String TEST_FO_DICTIONARY_NAME = "Test FO Dictionary";
    private final static String TEST_EMAIL_ADDRESS_1 = "testemail1@abc.com";
    private final static String TEST_EMAIL_ADDRESS_2 = "testemail2@abc.com";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserProjectPermissionRepository userProjectPermissionRepository;

    @Autowired
    private FunctionalOutputDataDictionaryRepository functionalOutputDataDictionaryRepository;

    @Autowired
    private AssetDataDictionaryRepository assetDataDictionaryRepository;

    @Autowired
    private TestEntityManager em;

    private final AssetDataDictionaryDAO add = AssetDataDictionaryDAO.builder().name(TEST_ASSET_DICTIONARY_NAME).build();

    private final FunctionalOutputDataDictionaryDAO fodd =
            FunctionalOutputDataDictionaryDAO.builder().name(TEST_FO_DICTIONARY_NAME).build();

    @BeforeEach
    void testRepoInjected() {
        assertThat(userRepository, notNullValue());
        assertThat(projectRepository, notNullValue());
        assertThat(permissionRepository, notNullValue());
        assertThat(userProjectPermissionRepository, notNullValue());
        assertThat(functionalOutputDataDictionaryRepository, notNullValue());
        assertThat(assetDataDictionaryRepository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreUserPermissions() {
        // Create 2 projects and 2 users
        // Create permissions for both users in both projects

        AssetDataDictionaryDAO add_test =  assetDataDictionaryRepository.save(add);
        logger.info("Saved assetDataDictionary: {}", add_test);

        FunctionalOutputDataDictionaryDAO fodd_test  = functionalOutputDataDictionaryRepository.save(fodd);
        logger.info("Saved functionalOutputDataDictionary: {}", fodd_test);

        ProjectDAO project1 = projectRepository.save(
                                    ProjectDAO.builder()
                                        .name(TEST_PROJECT_NAME_1)
                                        .assetDataDictionary(add_test)
                                        .foDataDictionary(fodd_test)
                                        .build());
        assertThat(project1.getId(), notNullValue());
        logger.info("Saved project1: {}", project1);
        ProjectDAO project2 = projectRepository.save(
                                    ProjectDAO.builder()
                                        .name(TEST_PROJECT_NAME_2)
                                        .assetDataDictionary(add_test)
                                        .foDataDictionary(fodd_test)
                                        .build());
        assertThat(project2.getId(), notNullValue());
        logger.info("Saved project2: {}", project2);


        UserDAO user1 = userRepository.save(
                                    UserDAO.builder()
                                        .emailAddress(TEST_EMAIL_ADDRESS_1)
                                        .isSuperUser(false)
                                        .build());
        logger.info("Saved user1: {}", user1);
        assertThat(user1.getId(), notNullValue());

        UserDAO user2 = userRepository.save(
            UserDAO.builder()
                .emailAddress(TEST_EMAIL_ADDRESS_2)
                .isSuperUser(false)
                .build());
        logger.info("Saved user2: {}", user2);
        assertThat(user1.getId(), notNullValue());

        // now create the user project permissions
        List<PermissionDAO> permissions = (List<PermissionDAO>) permissionRepository.findAll();
        logger.info("Permissions found:");
        for (UserDAO userDAO : new UserDAO[]{user1, user2}) {
            for (ProjectDAO projectDAO : new ProjectDAO[]{project1, project2}) {
                for (PermissionDAO permissionDao : permissions) {
                    UserProjectPermissionDAO userProjectPermission =
                        userProjectPermissionRepository.save(
                            UserProjectPermissionDAO.builder()
                                .user(userDAO)
                                .project(projectDAO)
                                .permission(permissionDao)
                                .build());
                    logger.info("Saved userProjectInfo: {}", userProjectPermission);
                }
            }
        }


        List<UserProjectPermissionDAO> userProjectPermissionsDAOs = (List<UserProjectPermissionDAO>) userProjectPermissionRepository.findAll();
        logger.info("UserProjectPermissions found:");
        for (UserProjectPermissionDAO userProjectPermissionsDAO : userProjectPermissionsDAOs) {
            logger.info("UserProjectPermission: " + userProjectPermissionsDAO);
        }
        // check total number of userprojectpermissions
        assertThat("Incorrect total number of userprojectpermissions found",
            userProjectPermissionsDAOs.stream().count(), equalTo(4 * permissions.stream().count()));

        // check number of userprojectpermissions for project1
        long countForProject1 = userProjectPermissionRepository.countByProject(project1);
        assertThat("Incorrect  number of userprojectpermissions found for project1",
                    countForProject1, equalTo(2 * permissions.stream().count()));

        // check number of userprojectpermissions for user1
        long countForUser1 = userProjectPermissionRepository.countByUser(user1);
        assertThat("Incorrect  number of userprojectpermissions found for user1",
            countForUser1, equalTo(2 * permissions.stream().count()));

        // check number of userprojectpermissions for user 1 and project2
        long countForUser1AndProject2 = userProjectPermissionRepository.countByUserAndProject(user1, project2);
        assertThat("Incorrect  number of userprojectpermissions found for user1 and project2",
            countForUser1AndProject2, equalTo(permissions.stream().count()));

        // check userprojectpermissions for user 2 and project2
       for (PermissionDAO permissionDAO : permissions) {
            UserProjectPermissionDAO userProjectPermissionDao
                //= userProjectPermissionRepository.findByUserDaoAndProjectDaoAndPermissionDoa(user2, project2, permissionDAO);
                = userProjectPermissionRepository.findByUserAndPermissionAndProject(user2, permissionDAO, project2);
           logger.info("Checking UserProjectPermission: " + userProjectPermissionDao);
            assertThat("Unexpected userprojectpermissions found for user2, project2 and permission " + permissionDAO,
                userProjectPermissionDao != null &&
                    userProjectPermissionDao.getUser().equals(user2) &&
                    userProjectPermissionDao.getProject().equals(project2) &&
                    userProjectPermissionDao.getPermission().equals(permissionDAO)
            );
        }
    }


}

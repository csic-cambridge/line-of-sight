package com.costain.cdbb.db.test;


import com.costain.cdbb.model.AssetDataDictionaryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(ProjectRepositoryTest.class);

    private static final String TEST_PROJECT_NAME = "Test Project";
    private static final String TEST_ASSET_DICTIONARY_NAME = "Test Asset Dictionary";
    private static final String TEST_FO_DICTIONARY_NAME = "Test FO Dictionary";
    private static final UUID SAMPLE_PROJECT_ID = UUID.fromString("387dac90-e188-11ec-8fea-0242ac120002");

    @Autowired
    private ProjectRepository repository;
    @Autowired
    private ProjectOrganisationalObjectiveRepository pooRepository;
    @Autowired
    private TestEntityManager em;

    private AssetDataDictionaryDAO add =
        AssetDataDictionaryDAO.builder().name(TEST_ASSET_DICTIONARY_NAME).build();

    private FunctionalOutputDataDictionaryDAO fodd =
        FunctionalOutputDataDictionaryDAO.builder().name(TEST_FO_DICTIONARY_NAME).build();

    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());
        em.flush();
    }

    @Test
    public void shouldStoreAProject() {
         ProjectDAO project = repository.save(
                                ProjectDAO.builder()
                                    .name(TEST_PROJECT_NAME)
                                    .assetDataDictionary(add)
                                    .foDataDictionary(fodd)
                                    .build());

        logger.info("Saved project: {}", project);

        assertThat(project.getId(), notNullValue());
        assertThat(project.getName(), equalTo(TEST_PROJECT_NAME));
        assertThat(project.getFoDataDictionary(), equalTo(fodd));
        assertThat(project.getAssetDataDictionary(), equalTo(add));
    }

    @Test
    public void copyProject() {
        // WIP
        // copy the existing sample project


        ProjectDAO project = repository.findById(SAMPLE_PROJECT_ID).orElseThrow();
        //List<ProjectDAO> projects = (List<ProjectDAO>) repository.findAll();
        //logger.info("Found {} projects", projects.stream().count());
        ProjectDAO newProject = repository.save(
            ProjectDAO.builder()
                .name("COPIED_PROJECT")
                .assetDataDictionary(project.getAssetDataDictionary())
                .foDataDictionary(project.getFoDataDictionary())
                .build());

        // create new set of POOs
        //List<ProjectOrganisationalObjectiveDAO> poos = (List<ProjectOrganisationalObjectiveDAO>) pooRepository.findAll();
        /*Stream<ProjectOrganisationalObjectiveDAO> newPoos = poos.stream().map(poo -> {
                                return pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                                     .projectDAO(newProject)
                                     .oo(poo.getOo())
                                     .acceptedLastModifiedTimestamp(poo.getAcceptedLastModifiedTimestamp())
                                     .build());
        });*/
     /*   Set<ProjectOrganisationalObjectiveDAO> newPoos = new HashSet<>();
        for(ProjectOrganisationalObjectiveDAO poo : poos) {
            pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                .projectDAO(newProject)
                .oo(poo.getOo())
                .acceptedLastModifiedTimestamp(poo.getAcceptedLastModifiedTimestamp())
                .build());
        }

        newProject.setProjectOrganisationalObjectiveDAOs(newPoos);
        newProject = repository.save(newProject);
        logger.info("Done project copy");*/

    }


}

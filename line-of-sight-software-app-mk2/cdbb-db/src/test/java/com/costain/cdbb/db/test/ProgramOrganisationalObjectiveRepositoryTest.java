package com.costain.cdbb.db.test;

import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectOrganisationalObjectiveRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProgramOrganisationalObjectiveRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalOutputRepositoryTest.class);

    private final String PROJECT_ID = "387dac90-e188-11ec-8fea-0242ac120002";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectOrganisationalObjectiveRepository pooRepository;

    @Autowired
    private OoVersionRepository oovRepository;

    @Autowired
    private FunctionalRequirementRepository frRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void testRepoInjected() {
        assertThat(pooRepository, notNullValue());
    }

    @Disabled
    @Test
    public void shouldStoreAnOo() {
        Optional<ProjectDAO> projectDao = projectRepository.findById(UUID.fromString(PROJECT_ID));
        FunctionalRequirementDAO linkedFr = FunctionalRequirementDAO.builder()
            .name("FR #1")
            .projectId(projectDao.get().getId())
            .build();
        linkedFr = frRepository.save(linkedFr);
        logger.info("Saved asset: {}", linkedFr);
        Iterable<OoVersionDAO> oovs = oovRepository.findAll();
        OoVersionDAO oov = oovs.iterator().next();
        ProjectOrganisationalObjectiveDAO poo = pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
            .projectId(projectDao.get().getId())
            .ooVersion(oov)
            .frs(Set.of(FunctionalRequirementDAO.builder().id(linkedFr.getId()).projectId(projectDao.get().getId()).build()))
            .build());

        logger.info("Saved poo: {}", poo);


        UUID id = poo.getId();

        //Simulate the transaction finishing
        em.flush();

        poo = pooRepository.findById(id).orElseThrow();

        assertThat(poo.getId(), notNullValue());
        assertThat(poo.getFrs().iterator().next().getId(), equalTo(linkedFr.getId()));
       //TO DO assertThat(poo.getFrs().iterator().next().getName(), equalTo(linkedFr.getName()));
    }

    @Disabled
    @Test
    public void shouldNotStoreNonExistentFo() {
        Optional<ProjectDAO> projectDao = projectRepository.findById(UUID.fromString(PROJECT_ID));
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            Iterable<OoVersionDAO> oovs = oovRepository.findAll();
            OoVersionDAO oov = oovs.iterator().next();
            ProjectOrganisationalObjectiveDAO poo = pooRepository.save(ProjectOrganisationalObjectiveDAO.builder()
                .ooVersion(oov)
                .frs(Set.of(FunctionalRequirementDAO.builder().id(UUID.randomUUID()).build()))
                    .projectId(projectDao.get().getId())
                .build());

            logger.info("Saved fo: {}", poo);

            em.flush();
        });

        assertThat(exception.getCause().getClass(), equalTo(ConstraintViolationException.class));
    }

}

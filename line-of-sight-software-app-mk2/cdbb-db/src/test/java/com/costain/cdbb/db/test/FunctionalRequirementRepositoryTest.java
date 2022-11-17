package com.costain.cdbb.db.test;

import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.ProjectDAO;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.ProjectRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class FunctionalRequirementRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalRequirementRepositoryTest.class);
    private final String PROJECT_ID = "387dac90-e188-11ec-8fea-0242ac120002";

    @Autowired
    private FunctionalRequirementRepository functionalRequirementRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FunctionalOutputRepository foRepository;
    @Autowired
    private FunctionalOutputDataDictionaryEntryRepository foDdeRepository;
    @Autowired
    private TestEntityManager em;


    private FunctionalOutputDataDictionaryEntryDAO getFodde () {
        FunctionalOutputDataDictionaryEntryDAO foDde =
            foDdeRepository.findByFoDictionaryIdAndEntryId(
                UUID.fromString("97ee7a74-e8c7-11ec-8fea-0242ac120002"), "EF_20_10_50");
        if (null == foDde) {
            foDde = foDdeRepository.save(FunctionalOutputDataDictionaryEntryDAO.builder()
                .entryId("EF_20_10_50").text("Membrane structures")
                .foDictionaryId(UUID.fromString("97ee7a74-e8c7-11ec-8fea-0242ac120002"))
                .build());
        }
        return foDde;
    }

    @BeforeEach
    void testRepoInjected() {
        assertThat(functionalRequirementRepository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnFR() {
        Optional<ProjectDAO> projectDao = projectRepository.findById(UUID.fromString(PROJECT_ID));
        assertThat("Project not found", projectDao.isPresent());
        final FunctionalOutputDAO linkedFo = foRepository.save(FunctionalOutputDAO.builder()
            .dataDictionaryEntry(getFodde())
            .projectId(projectDao.get().getId())
            .build());
        logger.info("Saved FO: {}", linkedFo);
        em.flush();
        FunctionalRequirementDAO fr = functionalRequirementRepository.save(FunctionalRequirementDAO.builder()
            .name("Functional Requirement #1")
            .fos(Set.of(FunctionalOutputDAO.builder().id(linkedFo.getId()).projectId(projectDao.get().getId()).build()))
            .projectId(projectDao.get().getId())
            .build());

        logger.info("Saved FR: {}", fr);

        UUID id = fr.getId();

        //Simulate the transaction finishing
        em.flush();
        em.clear();

       fr = functionalRequirementRepository.findById(id).orElseThrow();

        assertThat(fr.getId(), notNullValue());
        assertThat(fr.getName(), equalTo("Functional Requirement #1"));
        assertThat(fr.getProjectId(), equalTo(projectDao.get().getId()));
        assertThat(fr.getFos().iterator().next().getId(), equalTo(linkedFo.getId()));
        assertThat(fr.getFos().iterator().next().getDataDictionaryEntry().getText(),
                     equalTo(linkedFo.getDataDictionaryEntry().getText()));
    }

    @Test
    public void shouldNotStoreNonExistentFo() {
        Optional<ProjectDAO> projectDao = projectRepository.findById(UUID.fromString(PROJECT_ID));
        assertThat("Project not found", projectDao.isPresent());
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            FunctionalRequirementDAO fr = functionalRequirementRepository.save(FunctionalRequirementDAO.builder()
                .name("Functional Requirement #1")
                .fos(Set.of(FunctionalOutputDAO.builder().id(UUID.randomUUID()).build()))
                .projectId(projectDao.get().getId())
                .build());

            logger.info("Saved fo: {}", fr);

            em.flush();
        });

        assertThat(exception.getCause().getClass(), equalTo(ConstraintViolationException.class));
    }

}

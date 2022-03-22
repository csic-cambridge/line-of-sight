package com.costain.cdbb.db.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.costain.cdbb.model.FunctionalObjectiveDAO;
import com.costain.cdbb.model.FunctionalObjectiveDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.repositories.FunctionalObjectiveRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import java.util.Set;
import java.util.UUID;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class FunctionalRequirementRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalRequirementRepositoryTest.class);

    @Autowired
    private FunctionalRequirementRepository repository;
    @Autowired
    private FunctionalObjectiveRepository foRepository;

    @Autowired
    private TestEntityManager em;

    private FunctionalObjectiveDataDictionaryEntryDAO fodde = FunctionalObjectiveDataDictionaryEntryDAO.builder()
        .id("EF_20_10_50").text("Membrane structures").build();

    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnFR() {
        FunctionalObjectiveDAO linkedFo = FunctionalObjectiveDAO.builder().dataDictionaryEntry(fodde).build();
        linkedFo = foRepository.save(linkedFo);
        logger.info("Saved FO: {}", linkedFo);

        FunctionalRequirementDAO fr = repository.save(FunctionalRequirementDAO.builder()
            .name("Functional Requirement #1")
            .fos(Set.of(FunctionalObjectiveDAO.builder().id(linkedFo.getId()).build()))
            .build());

        logger.info("Saved FR: {}", fr);

        UUID id = fr.getId();

        //Simulate the transaction finishing
        em.flush();
        em.clear();

        fr = repository.findById(id).orElseThrow();

        assertThat(fr.getId(), notNullValue());
        assertThat(fr.getName(), equalTo("Functional Requirement #1"));
        assertThat(fr.getFos().iterator().next().getId(), equalTo(linkedFo.getId()));
        assertThat(fr.getFos().iterator().next().getDataDictionaryEntry().getText(),
            equalTo(linkedFo.getDataDictionaryEntry().getText()));
    }

    @Test
    public void shouldNotStoreNonExistentFo() {
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            FunctionalRequirementDAO fr = repository.save(FunctionalRequirementDAO.builder()
                .name("Functional Requirement #1")
                .fos(Set.of(FunctionalObjectiveDAO.builder().id(UUID.randomUUID()).build()))
                .build());

            logger.info("Saved fo: {}", fr);

            em.flush();
        });

        assertThat(exception.getCause().getClass(), equalTo(ConstraintViolationException.class));
    }

}

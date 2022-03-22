package com.costain.cdbb.db.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.costain.cdbb.model.FunctionalRequirementDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
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
public class OrganisationalObjectiveRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalObjectiveRepositoryTest.class);

    @Autowired
    private OrganisationalObjectiveRepository repository;

    @Autowired
    private FunctionalRequirementRepository frRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());
    }

    @Test
    public void shouldStoreAnOo() {
        FunctionalRequirementDAO linkedFr = FunctionalRequirementDAO.builder().name("FR #1").build();
        linkedFr = frRepository.save(linkedFr);
        logger.info("Saved asset: {}", linkedFr);

        OrganisationalObjectiveDAO oo = repository.save(OrganisationalObjectiveDAO.builder()
            .name("Organisational Objective #1")
            .oirs(Set.of("OIR #1", "OIR #2"))
            .frs(Set.of(FunctionalRequirementDAO.builder().id(linkedFr.getId()).build()))
            .build());

        logger.info("Saved oo: {}", oo);

        UUID id = oo.getId();

        //Simulate the transaction finishing
        em.flush();
        em.clear();

        oo = repository.findById(id).orElseThrow();

        assertThat(oo.getId(), notNullValue());
        assertThat(oo.getName(), equalTo("Organisational Objective #1"));
        assertThat(oo.getOirs(), equalTo(Set.of("OIR #1", "OIR #2")));
        assertThat(oo.getFrs().iterator().next().getId(), equalTo(linkedFr.getId()));
        assertThat(oo.getFrs().iterator().next().getName(), equalTo(linkedFr.getName()));
    }

    @Test
    public void shouldNotStoreNonExistentFo() {
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            OrganisationalObjectiveDAO oo = repository.save(OrganisationalObjectiveDAO.builder()
                .name("Organisational Objective #1")
                .oirs(Set.of("OIR #1", "OIR #2"))
                .frs(Set.of(FunctionalRequirementDAO.builder().id(UUID.randomUUID()).build()))
                .build());

            logger.info("Saved fo: {}", oo);

            em.flush();
        });

        assertThat(exception.getCause().getClass(), equalTo(ConstraintViolationException.class));
    }

}

package com.costain.cdbb.db.test;

import com.costain.cdbb.repositories.OoVersionRepository;
import com.costain.cdbb.repositories.OrganisationalObjectiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganisationalObjectiveRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalOutputRepositoryTest.class);


    @Autowired
    private OrganisationalObjectiveRepository ooRepository;

    @Autowired
    private OoVersionRepository ooVersionRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void testRepoInjected() {
        assertThat(ooRepository, notNullValue());
    }

    @Test
    public void shouldStoreAnOo() {
        //TO DO
       /* OrganisationalObjectiveDAO oo = ooRepository.save(OrganisationalObjectiveDAO.builder()
            .oirs(List.of("OIR #1", "OIR #2"))
            //.ooVersions(ooVersions)
            .build());

        List<OoVersionDAO> ooVersions = new ArrayList<>();
        ooVersions.add(ooVersionRepository.save(OoVersionDAO.builder().name("OOVersion - test 1").oo(oo).build()));
        ooVersions.add(ooVersionRepository.save(OoVersionDAO.builder().name("OOVersion - test 2").oo(oo).build()));

        //Simulate the transaction finishing
        em.flush();
        //em.clear();

        OrganisationalObjectiveDAO oo2 = ooRepository.findById(oo.getId()).orElseThrow();

        assertThat(oo2.getId(), notNullValue());
        assertThat(oo2.getOirs(), containsInAnyOrder("OIR #1", "OIR #2"));
        assertThat(oo2.getOoVersions(), equalTo(ooVersions));*/
    }

}

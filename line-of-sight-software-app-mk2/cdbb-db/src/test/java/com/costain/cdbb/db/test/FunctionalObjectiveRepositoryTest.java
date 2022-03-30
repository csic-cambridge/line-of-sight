package com.costain.cdbb.db.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalObjectiveDAO;
import com.costain.cdbb.model.FunctionalObjectiveDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FunctionalObjectiveRepository;
import java.util.Set;
import java.util.UUID;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class FunctionalObjectiveRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalObjectiveRepositoryTest.class);

    @Autowired
    private FunctionalObjectiveRepository repository;
    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private TestEntityManager em;

    private AssetDataDictionaryEntryDAO adde = AssetDataDictionaryEntryDAO.builder()
        .id("Ss_15_10_30_29").text("Earthworks filling systems around trees").build();
    private FunctionalObjectiveDataDictionaryEntryDAO fodde = FunctionalObjectiveDataDictionaryEntryDAO.builder()
        .id("EF_20_10_50").text("Membrane structures").build();

    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnFo() {

        AssetDAO linkedAsset = AssetDAO.builder().dataDictionaryEntry(adde).build();
        linkedAsset = assetRepository.save(linkedAsset);
        logger.info("Saved asset: {}", linkedAsset);

        FunctionalObjectiveDAO fo = repository.save(FunctionalObjectiveDAO.builder()
            .dataDictionaryEntry(fodde)
            .firs(Set.of("FIR #1", "FIR #2"))
            .assets(Set.of(AssetDAO.builder().id(linkedAsset.getId()).build()))
            .build());

        logger.info("Saved fo: {}", fo);

        UUID id = fo.getId();

        //Simulate the transaction finishing
        em.flush();
        em.clear();

        fo = repository.findById(id).orElseThrow();

        assertThat(fo.getId(), notNullValue());
        assertThat(fo.getDataDictionaryEntry().getText(), equalTo(fodde.getText()));
        assertThat(fo.getFirs(), equalTo(Set.of("FIR #1", "FIR #2")));
        assertThat(fo.getAssets().iterator().next().getId(), equalTo(linkedAsset.getId()));
        assertThat(fo.getAssets().iterator().next().getDataDictionaryEntry().getText(), equalTo(linkedAsset.getDataDictionaryEntry().getText()));
    }

    @Test
    public void shouldNotStoreNonExistentAsset() {
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            FunctionalObjectiveDAO fo = repository.save(FunctionalObjectiveDAO.builder()
                .dataDictionaryEntry(fodde)
                .firs(Set.of("FIR #1", "FIR #2"))
                .assets(Set.of(AssetDAO.builder().id(UUID.randomUUID()).build()))
                .build());

            logger.info("Saved fo: {}", fo);

            em.flush();
        });

        assertThat(exception.getCause().getClass(), equalTo(ConstraintViolationException.class));
    }

    @Test
    public void shouldNotStoreNonExistentFodde() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            FunctionalObjectiveDAO fo = repository.save(FunctionalObjectiveDAO.builder()
                .dataDictionaryEntry(FunctionalObjectiveDataDictionaryEntryDAO.builder().id("rubbish").text("doesn't exist").build())
                .firs(Set.of("FIR #1", "FIR #2"))
                .build());

            logger.info("Saved fo: {}", fo);

            em.flush();
        });
    }

}

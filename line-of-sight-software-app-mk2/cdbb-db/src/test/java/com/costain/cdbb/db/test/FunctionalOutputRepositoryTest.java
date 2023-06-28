package com.costain.cdbb.db.test;

import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.model.FunctionalOutputDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FirsRepository;
import com.costain.cdbb.repositories.FunctionalOutputDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.FunctionalOutputRepository;
import com.costain.cdbb.repositories.FunctionalRequirementRepository;
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

import javax.persistence.PersistenceException;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class FunctionalOutputRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(FunctionalOutputRepositoryTest.class);
    private static final UUID SAMPLE_PROJECT_ID = UUID.fromString("387dac90-e188-11ec-8fea-0242ac120002");
    @Autowired
    private FunctionalOutputRepository repository;
    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetDataDictionaryEntryRepository assetDdeRepository;

    @Autowired
    private FunctionalOutputDataDictionaryEntryRepository foDdeRepository;

    @Autowired
    private FirsRepository firsRepository;

    @Autowired
    private TestEntityManager em;

    private static final boolean PERSIST = true;

    private AssetDataDictionaryEntryDAO getAdde() {
        AssetDataDictionaryEntryDAO adde =
            assetDdeRepository.findByAssetDictionaryIdAndEntryId(
                UUID.fromString("e1970a24-e8c7-11ec-8fea-0242ac120002"), "Ss_15_10_30_29");

        if (null == adde) {
            adde = assetDdeRepository.save(AssetDataDictionaryEntryDAO.builder()
                .entryId("Ss_15_10_30_29").text("Earthworks filling systems around trees")
                .assetDictionaryId(UUID.fromString("e1970a24-e8c7-11ec-8fea-0242ac120002"))
                .build());
        }
        return adde;
    }

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
        assertThat(repository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnFo() {

        AssetDAO linkedAsset = AssetDAO.builder().dataDictionaryEntry(getAdde()).build();
        linkedAsset = assetRepository.save(linkedAsset);
        logger.info("Saved asset: {}", linkedAsset);

        FunctionalOutputDAO fo = repository.save(FunctionalOutputDAO.builder()
            .projectId(SAMPLE_PROJECT_ID)
            .dataDictionaryEntry(getFodde())
            .assets(Set.of(AssetDAO.builder().id(linkedAsset.getId()).build()))
            .build());

        logger.info("Saved fo: {}", fo);

        UUID id = fo.getId();
        FirsDAO fir1 = makeFirsDao(1, fo, PERSIST);
        FirsDAO fir2 = makeFirsDao(2, fo, PERSIST);
        fo.setFirs(Set.of(fir1, fir2));

        //Simulate the transaction finishing
        em.flush();
        em.clear();

        fo = repository.findById(id).orElseThrow();

        assertThat(fo.getId(), notNullValue());
        assertThat(fo.getDataDictionaryEntry().getText(), equalTo(getFodde().getText()));
        assertThat(fo.getFirs(), equalTo(Set.of(fir1, fir2)));
        assertThat(fo.getAssets().iterator().next().getId(), equalTo(linkedAsset.getId()));
        assertThat(fo.getAssets().iterator().next().getDataDictionaryEntry().getText(), equalTo(linkedAsset.getDataDictionaryEntry().getText()));
    }

    @Test
    public void shouldNotStoreNonExistentAsset() {
        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            FirsDAO fir1 = makeFirsDao(1);
            FirsDAO fir2 = makeFirsDao(2);
            FunctionalOutputDAO fo = repository.save(FunctionalOutputDAO.builder()
                .dataDictionaryEntry(getFodde())
                .firs(Set.of(fir1, fir2))
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
            FirsDAO fir1 = makeFirsDao(1);
            FirsDAO fir2 = makeFirsDao(2);
            FunctionalOutputDAO fo = repository.save(FunctionalOutputDAO.builder()
                .dataDictionaryEntry(FunctionalOutputDataDictionaryEntryDAO.builder()
                    .entryId("rubbish").text("doesn't exist").foDictionaryId(UUID.randomUUID())
                    .build())
                .firs(Set.of(fir1, fir2))
                .build());

            logger.info("Saved fo: {}", fo);

            em.flush();
        });
    }

    private FirsDAO makeFirsDao(int n) {
        FirsDAO firsDao = FirsDAO.builder().firs("FIR #" + n).build();
        return firsDao;
    }

    private FirsDAO makeFirsDao(int n, FunctionalOutputDAO foDao, boolean persist) {
        FirsDAO firsDao = FirsDAO.builder().id(UUID.randomUUID().toString()).firs("FIR #" + n).foDao(foDao).build();
        if (persist) {
            return firsRepository.save(firsDao);
        }
        return firsDao;
    }
}

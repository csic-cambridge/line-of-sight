package com.costain.cdbb.db.test;


import com.costain.cdbb.model.AirsDAO;
import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FirsDAO;
import com.costain.cdbb.repositories.AirsRepository;
import com.costain.cdbb.repositories.AssetDataDictionaryEntryRepository;
import com.costain.cdbb.repositories.AssetRepository;
import com.costain.cdbb.repositories.FirsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class AssetRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(AssetRepositoryTest.class);
    private static final UUID SAMPLE_PROJECT_ID = UUID.fromString("387dac90-e188-11ec-8fea-0242ac120002");

    @Autowired
    private AssetRepository repository;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private AssetDataDictionaryEntryRepository assetDdeRepository;
    @Autowired
    private AirsRepository airsRepository;

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


    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnAsset() {
        AirsDAO air1 = makeAirsDao(1);
        AirsDAO air2 = makeAirsDao(2);
        Set<AirsDAO> airs = new HashSet<>();
        airs.add(air1);
        airs.add(air2);
        AssetDAO asset = AssetDAO.builder().dataDictionaryEntry(getAdde()).airs(airs).build();
        asset = repository.save(asset);

        logger.info("Saved asset: {}", asset);

        assertThat(asset.getId(), notNullValue());
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(getAdde().getText()));
        assertThat(asset.getAirs(), containsInAnyOrder(air1, air2));
    }

    @Test
    public void shouldUpdateAnAsset() {

        AssetDAO asset = repository.save(AssetDAO.builder()
            .projectId(SAMPLE_PROJECT_ID)
            .dataDictionaryEntry(getAdde())
            .build());

        logger.info("Saved asset: {}", asset);
        UUID id = asset.getId();

        AirsDAO air1 = makeAirsDao(1, asset, PERSIST);
        AirsDAO air2 = makeAirsDao(2, asset, PERSIST);

        Set<AirsDAO> airs = new HashSet<>();
        airs.add(air1);
        airs.add(air2);
        asset.setAirs(airs);

        assertThat(asset.getId(), notNullValue());
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(getAdde().getText()));
        assertThat(asset.getAirs(), equalTo(airs));

        AirsDAO air3 = makeAirsDao(3, asset, PERSIST);
        asset.getAirs().add(air3);
        repository.save(asset);

        em.flush();
        em.clear();

        repository.findById(id);
        assertThat(asset.getId(), equalTo(id));
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(getAdde().getText()));
      //  assertThat(asset.getAirs(), equalTo(Set.of(air1, air2, air3)));
    }

    @Test
    public void shouldNotStoreNonExistentDde() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            AssetDAO assetDao = AssetDAO.builder().dataDictionaryEntry(
                    AssetDataDictionaryEntryDAO.builder()
                        .entryId("invalid")
                        .text("Doesn't exist")
                        .assetDictionaryId(UUID.randomUUID())
                        .build())
                .airs(Set.of())
                .build();
            repository.save(assetDao);

            em.flush();
        });
    }

    private AirsDAO makeAirsDao(int n) {
        AirsDAO airsDao = AirsDAO.builder().airs("AIR #" + n).build();
        return airsDao;
    }

    private AirsDAO makeAirsDao(int n, AssetDAO assetDao, boolean persist) {
        AirsDAO airsDao = AirsDAO.builder().id(UUID.randomUUID().toString()).airs("AIR #" + n).assetDao(assetDao).build();
        //AirsDAO airsDao = AirsDAO.builder().id(UUID.randomUUID()).airs("AIR #" + n).asset_id(assetDao.getId().toString()).build();
        if (persist) {
            return airsRepository.save(airsDao);
        }
        return airsDao;
    }

    @Test
    public void createAndDeleteAsset () {
        AirsDAO air1 = makeAirsDao(1);
        AirsDAO air2 = makeAirsDao(2);
        Set<AirsDAO> airs = new HashSet<>();
        airs.add(air1);
        airs.add(air2);

        AssetDAO asset = AssetDAO.builder().projectId(SAMPLE_PROJECT_ID).dataDictionaryEntry(getAdde()).airs(airs).build();
        asset = repository.save(asset);
        em.flush();
        em.clear();
        repository.deleteById(asset.getId());
        em.flush();
        em.clear();
    }
}

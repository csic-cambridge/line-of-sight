package com.costain.cdbb.db.test;


import com.costain.cdbb.model.AssetDAO;
import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.repositories.AssetRepository;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class AssetRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(AssetRepositoryTest.class);
    private final static UUID SAMPLE_PROJECT_ID = UUID.fromString("387dac90-e188-11ec-8fea-0242ac120002");

    @Autowired
    private AssetRepository repository;
    @Autowired
    private TestEntityManager em;

    private AssetDataDictionaryEntryDAO adde =
        AssetDataDictionaryEntryDAO.builder().id("Ss_15_10_30_29").text("Membrane structures").build();

    @BeforeEach
    void testRepoInjected() {
        assertThat(repository, notNullValue());

        em.flush();
    }

    @Test
    public void shouldStoreAnAsset() {
        AssetDAO asset = repository.save(AssetDAO.builder().dataDictionaryEntry(adde)
            .airs(Set.of("AIR #1", "AIR #2")).build());

        logger.info("Saved asset: {}", asset);

        assertThat(asset.getId(), notNullValue());
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(adde.getText()));
        assertThat(asset.getAirs(), equalTo(Set.of("AIR #1", "AIR #2")));
    }

    @Test
    public void shouldUpdateAnAsset() {
        Set<String> airs = new HashSet<>();
        airs.add("AIR #1");
        airs.add("AIR #2");
        AssetDAO asset = repository.save(AssetDAO.builder()
                .projectId(SAMPLE_PROJECT_ID)
                .dataDictionaryEntry(AssetDataDictionaryEntryDAO.builder()
                    .id(adde.getId()).text(adde.getText())
                .build())
            .airs(airs).build());

        logger.info("Saved asset: {}", asset);

        UUID id = asset.getId();

        assertThat(asset.getId(), notNullValue());
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(adde.getText()));
        assertThat(asset.getAirs(), equalTo(Set.of("AIR #1", "AIR #2")));

        asset.getAirs().add("AIR #3");
        repository.save(asset);

        em.flush();
        em.clear();

        repository.findById(id);
        assertThat(asset.getId(), equalTo(id));
        assertThat(asset.getDataDictionaryEntry().getText(), equalTo(adde.getText()));
        assertThat(asset.getAirs(), equalTo(Set.of("AIR #1", "AIR #2", "AIR #3")));
    }

    @Test
    public void shouldNotStoreNonExistentDde() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            repository.save(AssetDAO.builder().dataDictionaryEntry(
                    AssetDataDictionaryEntryDAO.builder().id("invalid").text("Doesn't exist").build())
                .airs(Set.of()).build());

            em.flush();
        });
    }
}

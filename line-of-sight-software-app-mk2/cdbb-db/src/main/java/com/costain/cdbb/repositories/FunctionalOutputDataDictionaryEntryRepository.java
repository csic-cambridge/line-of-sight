package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface FunctionalOutputDataDictionaryEntryRepository extends CrudRepository<FunctionalOutputDataDictionaryEntryDAO, UUID> {
    Set<FunctionalOutputDataDictionaryEntryDAO> findByFoDictionaryIdOrderByEntryId(UUID foDictionaryId);
    Set<FunctionalOutputDataDictionaryEntryDAO> findByEntryId(String entryId);
    int deleteByFoDictionaryId(UUID foDictionaryId);
    FunctionalOutputDataDictionaryEntryDAO findByFoDictionaryIdAndEntryId(UUID dataDictionaryId, String entryId);
}

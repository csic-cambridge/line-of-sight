package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface FunctionalOutputDataDictionaryEntryRepository extends CrudRepository<FunctionalOutputDataDictionaryEntryDAO, String> {
    Set<FunctionalOutputDataDictionaryEntryDAO> findByFoDictionaryId(UUID foDictionaryId);
}

package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalOutputDataDictionaryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FunctionalOutputDataDictionaryRepository extends CrudRepository<FunctionalOutputDataDictionaryDAO, UUID> {
    FunctionalOutputDataDictionaryDAO findByName(String name);
}

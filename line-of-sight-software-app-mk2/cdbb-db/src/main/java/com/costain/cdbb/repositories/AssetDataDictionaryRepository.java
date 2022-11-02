package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AssetDataDictionaryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AssetDataDictionaryRepository extends CrudRepository<AssetDataDictionaryDAO, UUID> {
}

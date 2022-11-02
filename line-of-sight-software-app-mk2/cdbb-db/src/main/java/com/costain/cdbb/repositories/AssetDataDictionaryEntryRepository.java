package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface AssetDataDictionaryEntryRepository extends CrudRepository<AssetDataDictionaryEntryDAO, String> {
    Set<AssetDataDictionaryEntryDAO> findByAssetDictionaryId(UUID assetDictionaryId);
}

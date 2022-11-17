package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AssetDataDictionaryEntryDAO;
import com.costain.cdbb.model.FunctionalOutputDataDictionaryEntryDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface AssetDataDictionaryEntryRepository extends CrudRepository<AssetDataDictionaryEntryDAO, UUID> {
    Set<AssetDataDictionaryEntryDAO> findByAssetDictionaryIdOrderByEntryId(UUID assetDictionaryId);
    Set<AssetDataDictionaryEntryDAO> findByEntryId(String entryId);
    int deleteByAssetDictionaryId(UUID assetDictionaryId);
    AssetDataDictionaryEntryDAO findByAssetDictionaryIdAndEntryId(UUID dataDictionaryId, String entryId);

}

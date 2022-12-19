package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AssetDAO;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetDAO, UUID> {
    Set<AssetDAO> findByProjectId(UUID projectId);
    AssetDAO findByProjectIdAndDataDictionaryEntry_EntryId(UUID projectId, String entryId);
    Set<AssetDAO>findByProjectIdOrderByDataDictionaryEntry_EntryId(UUID projectId);
}

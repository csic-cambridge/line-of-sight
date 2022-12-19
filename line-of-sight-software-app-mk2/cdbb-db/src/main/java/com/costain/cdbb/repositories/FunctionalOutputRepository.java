package com.costain.cdbb.repositories;


import com.costain.cdbb.model.FunctionalOutputDAO;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface FunctionalOutputRepository extends CrudRepository<FunctionalOutputDAO, UUID> {
    Set<FunctionalOutputDAO> findByProjectId(UUID projectId);
    FunctionalOutputDAO findByProjectIdAndDataDictionaryEntry_EntryId(UUID projectId, String entryId);
    Set<FunctionalOutputDAO> findByProjectIdOrderByDataDictionaryEntry_EntryId(UUID projectId);
}

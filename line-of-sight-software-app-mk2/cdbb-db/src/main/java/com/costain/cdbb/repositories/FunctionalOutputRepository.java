package com.costain.cdbb.repositories;


import com.costain.cdbb.model.FunctionalOutputDAO;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface FunctionalOutputRepository extends CrudRepository<FunctionalOutputDAO, UUID> {
    Set<FunctionalOutputDAO> findByProjectId(UUID projectId);
}

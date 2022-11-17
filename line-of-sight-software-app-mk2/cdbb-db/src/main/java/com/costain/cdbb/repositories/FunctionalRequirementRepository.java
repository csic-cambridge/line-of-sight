package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalRequirementDAO;
import java.util.Set;
import java.util.UUID;

import com.costain.cdbb.model.ProjectDAO;
import org.springframework.data.repository.CrudRepository;

public interface FunctionalRequirementRepository extends CrudRepository<FunctionalRequirementDAO, UUID> {
    Set<FunctionalRequirementDAO> findByProjectIdOrderByNameAsc(UUID projectId);
    FunctionalRequirementDAO findByProjectIdAndName(UUID projectId, String name);
}

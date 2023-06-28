package com.costain.cdbb.repositories;

import com.costain.cdbb.model.ProjectOrganisationalObjectiveDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectOrganisationalObjectiveRepository extends CrudRepository<ProjectOrganisationalObjectiveDAO, UUID> {
    List<ProjectOrganisationalObjectiveDAO> findByProjectIdOrderByOoVersionNameAsc(UUID projectId);
    List<ProjectOrganisationalObjectiveDAO> findByProjectIdAndOoVersionOoIsDeletedFalse(UUID projectId);
    List<ProjectOrganisationalObjectiveDAO> findByProjectIdAndOoVersionOoIdAndOoVersionOoIsDeletedFalse
        (UUID projectId, UUID ooId);
}

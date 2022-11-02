package com.costain.cdbb.repositories;

import com.costain.cdbb.model.OoVersionDAO;
import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrganisationalObjectiveRepository extends CrudRepository<OrganisationalObjectiveDAO, UUID> {
    @Query(value = "SELECT * FROM organisational_objective "
        + " WHERE  is_deleted = :isDeleted "
        + " ORDER BY (SELECT name from oo_version WHERE oo_id = organisational_objective.id "
        + " ORDER BY date_created DESC LIMIT 1) ASC;",
        nativeQuery = true
    )
    List<OrganisationalObjectiveDAO> findByIsDeletedOrderByNameAsc(@Param("isDeleted") boolean isDeleted);
    Set<OrganisationalObjectiveDAO> findByIsDeleted(boolean isDeleted);
}

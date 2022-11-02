package com.costain.cdbb.repositories;

import com.costain.cdbb.model.OoVersionDAO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/*
CrudRepository for OoVersionDAO
 */
public interface OoVersionRepository extends CrudRepository<OoVersionDAO, UUID>, JpaSpecificationExecutor<OoVersionDAO> {
    List<OoVersionDAO> findByOo_Id(UUID ooId);

    // Get all current and later oo_versions for this project organisational objective
    // ordered in date creation (earliest and therefore current first)

    @Query(value ="SELECT * from oo_version WHERE oo_id = "
        + "(select oo_id FROM project_organisational_objective poo , oo_version"
        + " WHERE oov_id = oo_version.id AND poo.id = :pooId)"
        + " AND date_created >= "
        +    "(SELECT date_created FROM project_organisational_objective poo, oo_version"
        +     " WHERE poo.id = :pooId AND oo_version.id=oov_id)"
        + " ORDER BY date_created ASC", nativeQuery = true
    )
    List<OoVersionDAO>findNonDiscardedByPoo(@Param ("pooId") String pooId);
    List<OoVersionDAO>findByOoIdOrderByDateCreated(UUID ooId);
}

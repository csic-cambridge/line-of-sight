package com.costain.cdbb.repositories;

import com.costain.cdbb.model.OirDAO;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;



/*
CrudRepository for OirDAO
 */


public interface OirRepository extends CrudRepository<OirDAO, UUID>, JpaSpecificationExecutor<OirDAO> {
    public void deleteByOoId(String ooId);
    @Query(value = "SELECT oirs.* FROM deleted_oir, oirs "
        + " WHERE oir_id=oirs.id AND project_id=:projectId AND oo_id= :ooId",
        nativeQuery = true
    )
    public List<OirDAO>findDeletedOirsForProjectAndOo(@Param("projectId") String projectId, @Param("ooId") String ooId);
    public List<OirDAO>findByOoId(String ooId);
}

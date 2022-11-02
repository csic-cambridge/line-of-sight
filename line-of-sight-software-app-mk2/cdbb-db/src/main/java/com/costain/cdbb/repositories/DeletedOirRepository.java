package com.costain.cdbb.repositories;

import com.costain.cdbb.model.DeletedOirDAO;
import com.costain.cdbb.model.OirDAO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/*
CrudRepository for DeletedOirDAO
 */


public interface DeletedOirRepository extends CrudRepository<DeletedOirDAO, UUID>, JpaSpecificationExecutor<OirDAO> {
    int deleteByDeletedOirPk_OirId(String oirId);
}

package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FirsDAO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;



/*
CrudRepository for FirsDAO
 */


public interface FirsRepository extends CrudRepository<FirsDAO, UUID>, JpaSpecificationExecutor<FirsDAO> {
    public FirsDAO findByFoDaoIdAndFirs(UUID FoDaoId, String Firs);
}

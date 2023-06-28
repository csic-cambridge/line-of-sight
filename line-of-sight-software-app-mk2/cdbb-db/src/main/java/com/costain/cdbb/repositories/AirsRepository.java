package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AirsDAO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;



/*
CrudRepository for AirsDAO
 */


public interface AirsRepository extends CrudRepository<AirsDAO, UUID>, JpaSpecificationExecutor<AirsDAO> {
    public void deleteByAssetDao_id(UUID assetId);
    public AirsDAO findByAssetDaoIdAndAirs(UUID AssetDaoId, String Firs);
}

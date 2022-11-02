package com.costain.cdbb.repositories;

import com.costain.cdbb.model.PermissionDAO;
import org.springframework.data.repository.CrudRepository;

/*
CrudRepository for PermissionDAO
 */
public interface PermissionRepository extends CrudRepository<PermissionDAO, Integer> {
}

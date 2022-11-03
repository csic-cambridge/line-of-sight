package com.costain.cdbb.repositories;

import com.costain.cdbb.model.UserPermissionDAO;
import com.costain.cdbb.model.UserPermissionId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


/*
CrudRepository for UserPermissionsDAO
 */
public interface UserPermissionRepository extends CrudRepository<UserPermissionDAO, UserPermissionId> {
    List<UserPermissionDAO> findById_UserId(UUID userId);
    int deleteById_UserId(UUID userId);
}

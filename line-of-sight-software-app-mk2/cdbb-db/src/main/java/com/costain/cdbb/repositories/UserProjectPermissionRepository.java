package com.costain.cdbb.repositories;

import com.costain.cdbb.model.*;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;



/*
CrudRepository for UserProjectPermissionsDAO
 */
public interface UserProjectPermissionRepository extends CrudRepository<UserProjectPermissionDAO, UserProjectPermissionId> {
    List<UserProjectPermissionDAO> findById_UserIdAndId_ProjectId(UUID userId, UUID projectId);
    int deleteById_UserIdAndId_ProjectId(UUID userId, UUID projectId);
}

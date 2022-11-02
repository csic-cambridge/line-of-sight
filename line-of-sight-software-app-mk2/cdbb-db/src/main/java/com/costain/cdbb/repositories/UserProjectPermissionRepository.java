package com.costain.cdbb.repositories;

import com.costain.cdbb.model.*;
import org.springframework.data.repository.CrudRepository;

/*
CrudRepository for UserProjectPermissionDAO
 */
public interface UserProjectPermissionRepository extends CrudRepository<UserProjectPermissionDAO, UserProjectPermissionId> {
    long countByProject(ProjectDAO project);
    long countByUser(UserDAO user);
    long countByUserAndProject(UserDAO user, ProjectDAO project);
    UserProjectPermissionDAO findByUserAndPermissionAndProject(UserDAO user, PermissionDAO permission, ProjectDAO project);
}

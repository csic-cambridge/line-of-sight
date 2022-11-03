/*
 * Copyright © 2022 Costain Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.model.helpers;


import com.costain.cdbb.core.permissions.PermissionsComparator;
import com.costain.cdbb.core.permissions.ProjectPermissionTypes;
import com.costain.cdbb.model.PermissionType;
import com.costain.cdbb.model.ProjectPermissions;
import com.costain.cdbb.model.UserProjectPermissionDAO;
import com.costain.cdbb.model.UserProjectPermissionId;
import com.costain.cdbb.repositories.ProjectRepository;
import com.costain.cdbb.repositories.UserProjectPermissionRepository;
import com.costain.cdbb.repositories.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;






@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProjectPermissionsHelper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserProjectPermissionRepository userProjectPermissionRepository;

    @Autowired
    UserHelper userHelper;

    public ProjectPermissions fromDao(
        Principal principal,
        String userId,
        UUID projectId) {
        final List<PermissionType> permissionTypes = new ArrayList<>();
        final List<Integer> grantedPermissionIds = new ArrayList<>();
        ProjectPermissions dto = new ProjectPermissions();
        UUID userUuid = userHelper.getUserId(principal, userId);
        dto.userId(userUuid);
        dto.projectId(projectId);
        dto.permissions(permissionTypes);
        List<UserProjectPermissionDAO> userProjectPermissionDaos = userProjectPermissionRepository
            .findById_UserIdAndId_ProjectId(userUuid, projectId);
        if (!userProjectPermissionDaos.isEmpty()) {
            userProjectPermissionDaos.forEach(dao -> {
                PermissionType permission = new PermissionType();
                permission.setId(dao.getId().getPermissionId());
                permission.setName(ProjectPermissionTypes.getPermissionNameForId(dao.getId().getPermissionId()));
                permission.setIsGranted(Boolean.TRUE);
                permissionTypes.add(permission);
                grantedPermissionIds.add(dao.getId().getPermissionId());
            });
        }
        // add in permissions not granted
        ProjectPermissionTypes.projectPermissionTypes.entrySet().stream().forEach(e -> {
            if (!grantedPermissionIds.contains(e.getKey())) {
                PermissionType permissionType = new PermissionType();
                permissionType.setId(e.getKey());
                permissionType.setName(e.getValue());
                permissionType.setIsGranted(Boolean.FALSE);
                permissionTypes.add(permissionType);
            }
        });
        permissionTypes.sort(new PermissionsComparator());
        return dto;
    }

    public List<UserProjectPermissionDAO> fromDto(ProjectPermissions dto, UUID userId, UUID projectId) {
        List<UserProjectPermissionDAO> permissionDaos = new ArrayList<>();

        dto.getPermissions().forEach(permission -> {
            if (permission.getIsGranted()) {
                permissionDaos.add(UserProjectPermissionDAO.builder()
                    .id(new UserProjectPermissionId(userId, projectId, permission.getId()))
                    .build()
                );
            }
        });
        return permissionDaos;
    }


    public Integer savePermissions(List<UserProjectPermissionDAO> uppDaos, UUID userId, UUID projectId) {
        userProjectPermissionRepository.deleteById_UserIdAndId_ProjectId(userId, projectId);
        uppDaos.forEach(dao -> userProjectPermissionRepository.save(dao));
        return 1;
    }

    public ProjectPermissions toDto(UUID userId, UUID projectId) {
        return fromDao(null, userId.toString(), projectId);
    }
}

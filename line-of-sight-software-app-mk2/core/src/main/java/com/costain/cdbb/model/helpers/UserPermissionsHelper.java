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
import com.costain.cdbb.core.permissions.UserPermissionTypes;
import com.costain.cdbb.model.PermissionType;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.model.UserPermissionDAO;
import com.costain.cdbb.model.UserPermissionId;
import com.costain.cdbb.model.UserPermissions;
import com.costain.cdbb.repositories.UserPermissionRepository;
import com.costain.cdbb.repositories.UserProjectPermissionRepository;
import com.costain.cdbb.repositories.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;



@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserPermissionsHelper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Autowired
    UserProjectPermissionRepository userProjectPermissionRepository;

    @Autowired
    UserHelper userHelper;

    /* need to update authorities whenever a user...
    1) Logs in
    2) Has their permissions/super user status changed
    3) Opens a new project
    NOTE: the user whose authorities are being changed may not be the principal for the request!
    */

    public Set<GrantedAuthority> getAuthoritiesForUserWithEmailAddress(String emailAddress) {
        return getAuthoritiesForUserAndProjectWithEmailAddress(emailAddress, null);
    }

    public Set<GrantedAuthority> getAuthoritiesForUserAndProjectWithEmailAddress(String emailAddress, UUID projectId) {

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        UserDAO userDao = userRepository.findByEmailAddress(emailAddress);
        if (userDao == null) {
            // we have not seen this user before so create a user record
            System.out.println("Creating user record for new user");
            userDao = UserDAO.builder().emailAddress(emailAddress).isSuperUser(false).build();
            userRepository.save(userDao);
        }
        if (userDao.isSuperUser()) {
            mappedAuthorities.add(new SimpleGrantedAuthority(UserPermissionTypes.ROLE_ADMIN));
        } else {
            mappedAuthorities.add(new SimpleGrantedAuthority(UserPermissionTypes.ROLE_USER));
            // set organisational permissions
            userPermissionRepository.findById_UserId(userDao.getUserId()).forEach(userPermissionDao -> {
                mappedAuthorities.add(new SimpleGrantedAuthority(
                    UserPermissionTypes.getAuthorityNameForId(userPermissionDao.getId().getPermissionId())));
            });
            if (projectId != null) {
                userProjectPermissionRepository.findById_UserIdAndId_ProjectId(userDao.getUserId(), projectId)
                    .forEach(userPermissionDao -> {
                        mappedAuthorities.add(new SimpleGrantedAuthority(
                            ProjectPermissionTypes.getAuthorityNameForId(userPermissionDao.getId().getPermissionId())));
                    });
            }
        }
        return mappedAuthorities;
    }

    public UserPermissions fromDao(Principal principal, String userId) {
        final List<PermissionType> permissionTypes = new ArrayList<>();
        final List<Integer> grantedPermissionIds = new ArrayList<>();
        UserPermissions dto = new UserPermissions();
        UUID userUuid = userHelper.getUserId(principal, userId);
        dto.userId(userUuid);
        dto.permissions(permissionTypes);
        List<UserPermissionDAO> userPermissionDaos = userPermissionRepository
            .findById_UserId(userUuid);
        if (!userPermissionDaos.isEmpty()) {
            userPermissionDaos.forEach(dao -> {
                PermissionType permission = new PermissionType();
                permission.setId(dao.getId().getPermissionId());
                permission.setName(UserPermissionTypes.getPermissionNameForId(dao.getId().getPermissionId()));
                permission.setIsGranted(Boolean.TRUE);
                permissionTypes.add(permission);
                grantedPermissionIds.add(dao.getId().getPermissionId());
            });
        }
        // add in permissions not granted
        UserPermissionTypes.userPermissionTypes.entrySet().stream().forEach(e -> {
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

    public List<UserPermissionDAO> fromDto(UserPermissions dto, UUID userId) {
        List<UserPermissionDAO> permissionDaos = new ArrayList<>();

        dto.getPermissions().forEach(permission -> {
            if (permission.getIsGranted()) {
                permissionDaos.add(UserPermissionDAO.builder()
                    .id(new UserPermissionId(userId, permission.getId()))
                    .build()
                );
            }
        });
        return permissionDaos;
    }

    public Integer savePermissions(List<UserPermissionDAO> upDaos, UUID userId) {
        userPermissionRepository.deleteById_UserId(userId);
        upDaos.forEach(dao -> userPermissionRepository.save(dao));
        return 1;
    }

    public UserPermissions toDto(UUID userId) {
        return fromDao(null, userId.toString());
    }
}

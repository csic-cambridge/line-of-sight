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


import com.costain.cdbb.core.events.ClientNotification;
import com.costain.cdbb.core.events.EventType;
import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.model.User;
import com.costain.cdbb.model.UserDAO;
import com.costain.cdbb.repositories.UserRepository;
import java.security.Principal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Provides helper functions for managing and manipulating users.
 */

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserHelper {
    private static final String ME = "me";

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Get the user id (as UUID) from passed String userId.
     * @param principal Required only if userId = "me"
     * @param userId "me" or actual userId
     * @return UUID userId
     */
    @Nullable
    public UUID getUserId(Principal principal, String userId) {
        if (ME.equalsIgnoreCase(userId)) {
            return getUserIdForEmailAddress(getEmailAddress(principal));
        }
        return UUID.fromString(userId);
    }

    /**
     * Get user dto from dao.
     * @param dao The user dao
     * @return User the user dto
     */
    public User fromDao(UserDAO dao) {
        User dto = new User();
        dto.userId(dao.getUserId());
        dto.emailAddress(dao.getEmailAddress());
        dto.isSuperUser(dao.isSuperUser());
        return dto;
    }

    /**
     * Save an update to the user.
     * @param userDao userDAO of the user
     * @return UserDAO the saved user
     */
    public UserDAO save(UserDAO userDao) {
        UserDAO savedUser = userRepository.save(userDao);
        applicationEventPublisher.publishEvent(new NotifyClientEvent(
            new ClientNotification(EventType.USER_PERMISSION_CHANGED, userDao.getUserId())));
        return savedUser;
    }

    /**
     * Get user dao from user id and user dto.
     * @param id user id
     * @param user user dto
     * @return UserDAO the user
     */
    public UserDAO fromDto(UUID id, User user) {
        return fromDto(UserDAO.builder().userId(id), user.getEmailAddress(), user.getIsSuperUser());
    }

    private UserDAO fromDto(UserDAO.UserDAOBuilder builder, String emailAddress, boolean isSuperUser) {
        return builder
            .emailAddress(emailAddress)
            .isSuperUser(isSuperUser).build();
    }

    /**
     * Get updated user dao from user id and user dto.
     * @param id user id
     * @param user dto
     * @return UserDAO user dao
     */
    public UserDAO updateFromDto(UUID id, User user) {
        UserDAO userDao = userRepository.findById(id).get();
        return fromDto(UserDAO.builder().userId(id), userDao.getEmailAddress(), user.getIsSuperUser());
    }

    /**
     * Get user id for a given email address.
     * @param emailAddress of required user
     * @return UUID id of user with email address
     */
    @Nullable
    public UUID getUserIdForEmailAddress(String emailAddress) {
        if (emailAddress == null) {
            return null;
        }
        UserDAO userDao = userRepository.findByEmailAddress(emailAddress);
        if (userDao == null) {
            return null;
        }
        return userDao.getUserId();
    }

    /**
     * Get the email address of the user from the Principal object.
     * @param principal object
     * @return String Email address
     */
    public String getEmailAddress(Object principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        return "unknown user";
    }
}

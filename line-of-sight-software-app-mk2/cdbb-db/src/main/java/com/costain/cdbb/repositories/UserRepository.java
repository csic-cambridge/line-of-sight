package com.costain.cdbb.repositories;

import com.costain.cdbb.model.UserDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/*
CrudRepository for UserDAO
 */
public interface UserRepository extends CrudRepository<UserDAO, UUID> {
    List<UserDAO> findAllByOrderByEmailAddressAsc();
    UserDAO findByEmailAddress(String emailAddress);
}

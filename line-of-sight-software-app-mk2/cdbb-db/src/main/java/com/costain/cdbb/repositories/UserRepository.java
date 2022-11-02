package com.costain.cdbb.repositories;

import com.costain.cdbb.model.UserDAO;
import org.springframework.data.repository.CrudRepository;

/*
CrudRepository for UserDAO
 */
public interface UserRepository extends CrudRepository<UserDAO, Integer> {
}

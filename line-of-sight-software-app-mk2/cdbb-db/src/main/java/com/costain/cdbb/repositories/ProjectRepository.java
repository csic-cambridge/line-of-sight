package com.costain.cdbb.repositories;

import com.costain.cdbb.model.ProjectDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends CrudRepository<ProjectDAO, UUID> {
    List<ProjectDAO> findAllByOrderByNameAsc();
    List<ProjectDAO> findByName(String name);
}

package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalRequirementDAO;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface FunctionalRequirementRepository extends CrudRepository<FunctionalRequirementDAO, UUID> {
}

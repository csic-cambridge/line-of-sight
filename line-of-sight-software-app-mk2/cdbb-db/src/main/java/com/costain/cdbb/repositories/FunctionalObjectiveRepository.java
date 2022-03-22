package com.costain.cdbb.repositories;

import com.costain.cdbb.model.FunctionalObjectiveDAO;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface FunctionalObjectiveRepository extends CrudRepository<FunctionalObjectiveDAO, UUID> {
}

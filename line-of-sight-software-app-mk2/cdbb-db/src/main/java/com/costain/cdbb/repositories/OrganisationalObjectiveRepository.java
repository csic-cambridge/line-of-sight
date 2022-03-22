package com.costain.cdbb.repositories;

import com.costain.cdbb.model.OrganisationalObjectiveDAO;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface OrganisationalObjectiveRepository extends CrudRepository<OrganisationalObjectiveDAO, UUID> {
}

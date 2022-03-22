package com.costain.cdbb.repositories;

import com.costain.cdbb.model.AssetDAO;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetDAO, UUID> {
}

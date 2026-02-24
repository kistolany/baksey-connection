package com.microservices.inventory_service.infrastructure.repository;

import com.microservices.inventory_service.infrastructure.repository.entity.InventoryEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID>, JpaSpecificationExecutor<InventoryEntity> {
    List<InventoryEntity> findAllByUuidAndProductId(UUID uuid, UUID productId);

    List<InventoryEntity> findByProductIdIn(List<UUID> productIds);

    Optional<InventoryEntity> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryEntity> findWithLockByProductId(UUID productId);
}

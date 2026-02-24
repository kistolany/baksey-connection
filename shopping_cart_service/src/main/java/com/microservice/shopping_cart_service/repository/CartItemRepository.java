package com.microservice.shopping_cart_service.repository;

import com.microservice.shopping_cart_service.repository.entity.CartItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {

    // "CartUuid" tells Spring to look inside the 'cart' field for its 'uuid'
    Optional<CartItemEntity> findByCartUuidAndProductId(UUID cartUuid, UUID productId);

    @Modifying
    @Transactional
    void deleteByCartUuidAndProductId(UUID cartUuid, UUID productId);

    // Also useful for calculating the total:
    List<CartItemEntity> findAllByCartUuid(UUID cartUuid);

    @Modifying
    @Transactional
    void deleteByCartUuidAndProductIdIn(UUID cartUuid, List<UUID> productIds);
}

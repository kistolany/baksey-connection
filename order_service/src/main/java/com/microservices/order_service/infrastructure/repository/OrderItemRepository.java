package com.microservices.order_service.infrastructure.repository;

import com.microservices.order_service.infrastructure.repository.entity.OrderItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
    List<OrderItemEntity> findAllByOrderId(UUID orderId);

    List<OrderItemEntity> findByOrderIdAndProductId(UUID orderId, UUID productId);

    @Transactional
    void deleteByOrderId(UUID orderUuid);
}

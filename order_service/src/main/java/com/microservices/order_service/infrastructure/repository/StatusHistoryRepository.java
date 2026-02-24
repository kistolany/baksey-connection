package com.microservices.order_service.infrastructure.repository;

import com.microservices.order_service.infrastructure.repository.entity.OrderStatusHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {
    List<OrderStatusHistoryEntity> findAllByOrderUuid(UUID orderId);
    @Transactional
    void deleteByOrderUuid(UUID orderUuid);
}

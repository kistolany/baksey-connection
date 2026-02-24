package com.microservices.order_service.infrastructure.repository;

import com.microservices.order_service.infrastructure.repository.entity.OrderEntity;
import com.microservices.order_service.infrastructure.repository.entity.OrderItemEntity;
import com.microservices.order_service.infrastructure.repository.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query(value = "SELECT " +
            "CAST(o.delivery_address_id AS CHAR) as addressId, " +
            "COUNT(o.uuid) as totalOrders, " +
            "SUM(o.total_amount) as totalSales " +
            "FROM orders o " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY o.delivery_address_id", nativeQuery = true)
    List<Map<String, Object>> getSalesByProvince();
}

package com.microservices.order_service.domain.db_repo;

import com.microservices.order_service.domain.constant.Constants.*;
import com.microservices.order_service.domain.model.OrderItemModel;
import com.microservices.order_service.domain.model.OrderModel;
import com.microservices.order_service.domain.model.TrackModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface OrderDomainRepo {
    Optional<OrderModel> getOneOrder(UUID orderId);

    List<TrackModel> trackingStatus(UUID orderId);

    void updateStatus(UUID orderId, OrderStatusEnum newStatus);

    Page<OrderModel> listAllOrders(Pageable pageable);

    OrderModel saveOrder(UUID customerId,UUID deliveryAddressId, BigDecimal totalAmount);

    void saveAllItems(UUID orderId, List<OrderItemModel> items);

    List<OrderItemModel> findAllByOrderId(UUID orderId);

    void cancelOrder(UUID orderId);

    List<Map<String, Object>> getSalesByProvinceRaw();
}

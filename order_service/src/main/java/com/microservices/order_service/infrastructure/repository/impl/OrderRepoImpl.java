package com.microservices.order_service.infrastructure.repository.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.exception.ApiException;
import com.microservices.order_service.domain.constant.Constants.CurrencyEnum;
import com.microservices.order_service.domain.constant.Constants.OrderStatusEnum;
import com.microservices.order_service.domain.db_repo.OrderDomainRepo;
import com.microservices.order_service.domain.model.OrderItemModel;
import com.microservices.order_service.domain.model.OrderModel;
import com.microservices.order_service.domain.model.TrackModel;
import com.microservices.order_service.domain.outbound.feingclient.AddressFeignClient;
import com.microservices.order_service.domain.outbound.respone.AddressResponse;
import com.microservices.order_service.infrastructure.repository.OrderItemRepository;
import com.microservices.order_service.infrastructure.repository.OrderRepository;
import com.microservices.order_service.infrastructure.repository.StatusHistoryRepository;
import com.microservices.order_service.infrastructure.repository.entity.OrderEntity;
import com.microservices.order_service.infrastructure.repository.entity.OrderItemEntity;
import com.microservices.order_service.infrastructure.repository.entity.OrderStatusHistoryEntity;
import com.microservices.order_service.infrastructure.repository.repoMapper.OrderItemRepoMapper;
import com.microservices.order_service.infrastructure.repository.repoMapper.OrderRepoMapper;
import com.microservices.common_service.domain.ResponseModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderRepoImpl implements OrderDomainRepo {
    private final OrderRepoMapper orderRepoMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemRepoMapper orderItemRepoMapper;
    private final StatusHistoryRepository historyRepository;
    private final AddressFeignClient addressFeignClient;

    @Override
    @Transactional
    public OrderModel saveOrder(UUID customerId,UUID deliveryAddressId, BigDecimal totalAmount) {
        OrderEntity entity = new OrderEntity();
        entity.setUserId(customerId);
        entity.setDeliveryAddressId(deliveryAddressId);
        entity.setOrderDate(LocalDateTime.now());
        entity.setTotalAmount(totalAmount);
        entity.setStatus(OrderStatusEnum.PENDING);
        entity.setCurrency(CurrencyEnum.USD);

        // Save order entity
        OrderEntity savedOrder = orderRepository.save(entity);

        // Call order status entity for save
        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        history.setOrder(savedOrder);
        history.setStatus(OrderStatusEnum.PENDING);
        history.setTracking_at(LocalDateTime.now());

        // save order status history
        historyRepository.save(history);

        // Return the saved order as a model
        return orderRepoMapper.toOrderModel(savedOrder);
    }

    @Override
    @Transactional
    public void saveAllItems(UUID orderId, List<OrderItemModel> items) {
        List<OrderItemEntity> entities = items.stream().map(model -> {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setUuid(model.getUuid());
            entity.setOrderId(orderId);
            entity.setProductId(model.getProductId());
            entity.setQuantity(model.getQuantity());
            entity.setUnitPrice(model.getUnitPrice());
            entity.setSubTotal(model.getSubTotal());
            entity.setStatus(model.getStatus());
            entity.setCurrency(CurrencyEnum.USD);
            return entity;
        }).toList();

        // Save all order items
        orderItemRepository.saveAll(entities);
    }

    @Override
    public List<OrderItemModel> findAllByOrderId(UUID orderId) {
        return orderItemRepository.findAllByOrderId(orderId)
                .stream().map(orderItemRepoMapper::toOrderItemModel).toList();
    }

    @Override
    public Optional<OrderModel> getOneOrder(UUID orderId) {
        // map entity to model and return
        return orderRepository.findById(orderId)
                .map(orderRepoMapper::toOrderModel);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {

        // 1. Remove status history records
        historyRepository.deleteByOrderUuid(orderId);

        // 2. Remove order items
        orderItemRepository.deleteByOrderId(orderId);

        // 3. Finally, remove the order
        orderRepository.deleteById(orderId);
    }

    @Override
    public Page<OrderModel> listAllOrders(Pageable pageable) {

        Page<OrderEntity> listAll = orderRepository.findAll(pageable);

        // Map entities to models and return
        return listAll.map(orderRepoMapper::toOrderModel);
    }

    @Override
    @Transactional
    public void updateStatus(UUID orderId, OrderStatusEnum newStatus) {

        // 1. Validate order id
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                        "Order not found"));

        // Validate for Exist status
        if (order.getStatus() == newStatus) {
            log.info("Order {} is already in status {}", orderId, newStatus);
            return;
        }

        // 2. Set new status and save order
        order.setStatus(newStatus);
        orderRepository.save(order);

        // 3. Create new status for OrderStatusEntity
        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setTracking_at(LocalDateTime.now());

        // save status order
        historyRepository.save(history);
        log.info("Order {} status updated to  {} and order history logged", orderId, newStatus);
    }

    @Override
    public List<TrackModel> trackingStatus(UUID orderId) {

        // 1. Fetch history records from order
        List<OrderStatusHistoryEntity> historyEntities = historyRepository.findAllByOrderUuid(orderId);

        // 2. Map entities to models and return
        return historyEntities.stream()
                .map(orderRepoMapper::toTrackModel)
                .toList();
    }

    @Override
    public List<Map<String, Object>> getSalesByProvinceRaw() {
        log.info("Start: Fetching raw sales data by province from database");

        try {
            // Fetch raw sales data grouped by delivery_address_id
            List<Map<String, Object>> rawData = orderRepository.getSalesByProvince();

            if (rawData == null || rawData.isEmpty()) {
                log.warn("No sales data found by province");
                return Collections.emptyList();
            }

            log.info("Finish: Retrieved {} address groups from database", rawData.size());
            return rawData;

        } catch (Exception e) {
            log.error("Error fetching sales by province: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch sales by province", e);
        }
    }

}

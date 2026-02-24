package com.microservices.order_service.application.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID uuid;
    private String currency;
    private String status;
    private BigDecimal totalAmount;
    private UUID userId;
    private LocalDateTime orderDate;
    private UUID deliveryAddressId;
    private List<OrderItemRequest.OrderItem> items;
}

package com.microservices.order_service.domain.model;

import static com.microservices.order_service.domain.constant.Constants.OrderStatusEnum;
import static com.microservices.order_service.domain.constant.Constants.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderModel {
    private UUID uuid;
    private UUID userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    List<TrackModel> trackResponse;
    private CurrencyEnum currency;
    private OrderStatusEnum status;
    private UUID deliveryAddressId;
}

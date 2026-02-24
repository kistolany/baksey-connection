package com.microservices.order_service.application.respone;

import com.microservices.order_service.domain.outbound.respone.AddressResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID uuid;
    private UUID userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private String deliveryAddressSummary;
}

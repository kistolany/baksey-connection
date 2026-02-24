package com.microservices.order_service.application.respone;

import com.microservices.order_service.domain.outbound.respone.AddressResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderDetailResponse {
    private UUID uuid;
    private UUID userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String currency;
    private String status;

    private AddressResponse deliveryAddress;
}

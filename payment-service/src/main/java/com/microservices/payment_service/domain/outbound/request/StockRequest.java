package com.microservices.payment_service.domain.outbound.request;

import lombok.Data;

@Data
public class StockRequest {
    private String productId;
}

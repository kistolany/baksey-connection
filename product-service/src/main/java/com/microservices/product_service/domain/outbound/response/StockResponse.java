package com.microservices.product_service.domain.outbound.response;

import lombok.Data;

@Data
public class StockResponse {
    private String productId;
    private String productName;
    private int quantity;
}

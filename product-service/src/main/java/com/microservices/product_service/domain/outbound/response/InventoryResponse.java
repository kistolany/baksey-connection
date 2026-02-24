package com.microservices.product_service.domain.outbound.response;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryResponse {

    private UUID inventoryId;
    private UUID productId;
    private Integer availableStock;
    private String status;
}


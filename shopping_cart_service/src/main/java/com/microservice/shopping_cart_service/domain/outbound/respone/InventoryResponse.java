package com.microservice.shopping_cart_service.domain.outbound.respone;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryResponse {
    private Integer availableStock;
    private UUID productId;
}

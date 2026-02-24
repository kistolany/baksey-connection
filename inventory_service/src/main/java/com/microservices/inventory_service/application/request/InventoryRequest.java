package com.microservices.inventory_service.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    private UUID productId;
    private Integer availableStock;
}

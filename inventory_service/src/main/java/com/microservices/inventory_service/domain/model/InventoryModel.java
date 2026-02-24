package com.microservices.inventory_service.domain.model;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryModel {
    private UUID inventoryId;
    private UUID productId;
    private String productName;
    private  String imagePath;
    private Integer quantity;
    private Integer availableStock;
}

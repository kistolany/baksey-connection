package com.microservices.inventory_service.application.respone;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class InventoryResponse {
    private UUID inventoryId;
    private UUID productId;
    private String productName;
    private String categoryName;
    private BigDecimal salePrice;
    private String brandName;
    private List<String> imagePath;
    private Integer availableStock;
}

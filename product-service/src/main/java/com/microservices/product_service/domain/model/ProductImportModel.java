package com.microservices.product_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductImportModel {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String currency;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

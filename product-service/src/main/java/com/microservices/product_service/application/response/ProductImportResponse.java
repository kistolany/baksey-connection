package com.microservices.product_service.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportResponse {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String currency;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

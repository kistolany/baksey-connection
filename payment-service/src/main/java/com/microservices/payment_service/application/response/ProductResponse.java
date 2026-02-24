package com.microservices.payment_service.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID id;
    private String productName;
    private String description;
    private BigDecimal salePrice;
    private String imagePath;
    private Integer quantity;

    // IDs for reference
    private UUID categoryId;
    private UUID brandId;

    // New Fields for Display
    private String categoryName;
    private String brandName;
}

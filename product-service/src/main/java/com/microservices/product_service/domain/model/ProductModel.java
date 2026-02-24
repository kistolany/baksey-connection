package com.microservices.product_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductModel {
    private UUID id;
	private String name;
	private String description;
    private BigDecimal salePrice;
    private Integer quantity;
    private List<String> images;
    private UUID categoryId;
    private UUID brandId;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

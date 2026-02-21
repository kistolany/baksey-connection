package com.microservices.product_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductModel {
    private UUID id;
	private String productName;
   // private String brandName;
	private String description;
    private BigDecimal salePrice;
    private Integer quantity;
    private  String imagePath;
    private UUID categoryId;
    private UUID brandId;
}

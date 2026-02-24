package com.microservices.payment_service.application.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class ProductRequest {
    private String productName;
    private String description;
    private BigDecimal salePrice;
    private UUID categoryId;
    private UUID brandId;
}




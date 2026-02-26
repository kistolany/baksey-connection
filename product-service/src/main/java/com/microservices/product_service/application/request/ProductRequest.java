package com.microservices.product_service.application.request;

import com.microservices.product_service.domain.constant.Constants.ProductStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal salePrice;
    private UUID categoryId;
    private UUID brandId;
    private ProductStatus status;
}

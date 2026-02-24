package com.microservice.shopping_cart_service.domain.outbound.respone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    @JsonProperty("id")
    private String productId;
    private String productName;
    private String description;
    private BigDecimal salePrice;
    private String imagePath;
}

package com.microservices.order_service.domain.outbound.respone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    @JsonProperty("id")
    private String productId;
    @JsonProperty("name")
    private String productName;
    private String description;
    @JsonProperty("price")
    private BigDecimal salePrice;
    private List<String> images;
}

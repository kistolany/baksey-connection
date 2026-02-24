package com.microservices.product_service.application.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID id;

    private String name;
    private String description;
    @JsonProperty("price")
    private BigDecimal salePrice;

    @JsonProperty("images")
    private List<String> images;

    private Integer quantity;
    private CategoryResponse category;
    private BrandResponse brand;

    @JsonProperty("creationAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime lastUpdatedAt;
}

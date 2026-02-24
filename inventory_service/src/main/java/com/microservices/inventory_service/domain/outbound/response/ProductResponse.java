package com.microservices.inventory_service.domain.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    @JsonProperty("id")
    private String productId;
    private String name;

    @JsonProperty("price")
    private BigDecimal salePrice;

    @JsonProperty("images")
    private List<String> images;

    // Use the DTOs defined above
    private CategoryResponse category;
    private BrandResponse brand;

    // These methods fix the "getter" issues in your ServiceImpl
    public String getCategoryName() {
        return (category != null) ? category.getName() : "N/A";
    }

    public String getBrandName() {
        return (brand != null) ? brand.getName() : "N/A";
    }
}

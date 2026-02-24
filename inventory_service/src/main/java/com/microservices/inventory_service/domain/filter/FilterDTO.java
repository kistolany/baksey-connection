package com.microservices.inventory_service.domain.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilterDTO {
    private Integer minStock;
    private Integer maxStock;
    private String productName;
    private String categoryName;
    private String brandName;
}
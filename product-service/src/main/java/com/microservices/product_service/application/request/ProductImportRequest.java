package com.microservices.product_service.application.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Price per unit is required")
    @Min(value = 01, message = "Price per unit must be greater than or equal to 0")
    private BigDecimal pricePerUnit;

    @Size(max = 10, message = "Currency must be ISO code (10 characters)")
    private String currency;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}

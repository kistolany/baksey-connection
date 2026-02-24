package com.microservices.order_service.application.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class OrderItemRequest {
    @Valid
    @NotEmpty(message = "Items list cannot be empty")
    private List<OrderItem> items;
    private UUID orderId;

    @Data
    public static class OrderItem{
        @NotNull(message = "Product ID is required")
        private UUID productId;
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }

}

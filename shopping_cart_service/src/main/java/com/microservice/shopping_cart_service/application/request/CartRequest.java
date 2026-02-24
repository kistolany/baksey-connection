package com.microservice.shopping_cart_service.application.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    private UUID userId;
    private List<ItemRequest> items; // Changed to match class name

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest { // Made public static and renamed
        private UUID productId;
        private Integer quantity;
    }
}



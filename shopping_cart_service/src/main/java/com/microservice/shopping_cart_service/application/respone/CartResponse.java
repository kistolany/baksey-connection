package com.microservice.shopping_cart_service.application.respone;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Data
public class CartResponse {
    private UUID uuid;
    private UUID userId;
    private String status;
    private String currency;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;

    @Data
    public static class CartItemResponse {
        private UUID uuid;
        private UUID productId;
        private Integer quantity;
        private String productName;
        private String productImage;
        private BigDecimal unitPrice;
        private BigDecimal subTotal;
        private String status;
    }
}

package com.microservices.order_service.application.respone;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID productId;
    private List<items> items;

    @Data
    public static class items{
        private UUID uuid;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String productName;
        private String productImage;
        private String description;
        private String currency;
        private BigDecimal subTotal;
        private String status;
    }
}

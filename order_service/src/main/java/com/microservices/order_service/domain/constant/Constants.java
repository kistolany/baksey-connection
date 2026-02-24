package com.microservices.order_service.domain.constant;

public class Constants {
    public enum OrderStatusEnum {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    public enum CurrencyEnum {
        USD,
        KHR,
        EUR,
    }

    public enum CartItemStaus {
        ACTIVE,
        INACTIVE
    }
}

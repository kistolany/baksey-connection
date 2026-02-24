package com.microservices.order_service.domain.model;

import static com.microservices.order_service.domain.constant.Constants.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemModel {
    private UUID uuid;
    private UUID productId;
    private UUID orderId;
    private int quantity;
    private BigDecimal unitPrice;
    private String productName;
    private String description;
    private String productImage;
    private BigDecimal subTotal;
    private CurrencyEnum currency;
    private String status;
}

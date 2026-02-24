package com.microservice.shopping_cart_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartModel{
    private UUID uuid;
    private UUID userId;
    private String status;
    private String currency;
    private BigDecimal totalAmount;
    private List<CartItemModel> items;

}

package com.microservice.shopping_cart_service.domain.model;

import com.microservice.shopping_cart_service.repository.entity.CartEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemModel {
    private UUID uuid;
    private UUID productId;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
    private String productName;
    private String productImage;
    private String status;
    private String cartId;
}

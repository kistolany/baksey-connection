package com.microservice.shopping_cart_service.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "cart_item")
public class CartItemEntity extends BaseEntity {
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private String status;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;
}


package com.microservice.shopping_cart_service.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "cart")
public class CartEntity extends BaseEntity {
    private UUID userId;
    private String status;
    private String currency;
    private BigDecimal totalAmount;
}
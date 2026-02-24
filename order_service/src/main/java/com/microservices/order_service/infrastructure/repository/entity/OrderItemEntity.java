package com.microservices.order_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import static com.microservices.order_service.domain.constant.Constants.CurrencyEnum;
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
@Table(name = "order_item")
public class OrderItemEntity extends BaseEntity {
    private UUID productId;
    private UUID orderId;
    private int quantity;
    private BigDecimal unitPrice;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
    private String status;
    private BigDecimal subTotal;

}
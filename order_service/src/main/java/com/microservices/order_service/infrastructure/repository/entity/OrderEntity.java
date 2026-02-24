package com.microservices.order_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import static com.microservices.order_service.domain.constant.Constants.OrderStatusEnum;
import static com.microservices.order_service.domain.constant.Constants.CurrencyEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {
    private UUID userId;
    private UUID deliveryAddressId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
    @Enumerated(EnumType.STRING) private OrderStatusEnum status;
    @OneToMany
    private List<OrderStatusHistoryEntity> statusHistories;
}

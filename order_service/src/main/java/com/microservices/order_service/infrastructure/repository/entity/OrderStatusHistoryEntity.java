package com.microservices.order_service.infrastructure.repository.entity;

import static com.microservices.order_service.domain.constant.Constants.OrderStatusEnum;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.*;
@Data
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistoryEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "orderId")
    private OrderEntity order;
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    private LocalDateTime tracking_at;
}
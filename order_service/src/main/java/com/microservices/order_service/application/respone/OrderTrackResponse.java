package com.microservices.order_service.application.respone;

import jakarta.persistence.criteria.Order;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderTrackResponse {
        private Order status;
        private LocalDateTime tracking_at;
}
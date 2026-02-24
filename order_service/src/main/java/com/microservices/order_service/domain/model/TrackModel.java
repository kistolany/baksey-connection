package com.microservices.order_service.domain.model;

import static com.microservices.order_service.domain.constant.Constants.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class TrackModel {
  //  private UUID orderId;
    private OrderStatusEnum status;
    private LocalDateTime tracking_at;
}

package com.microservices.order_service.infrastructure.repository.repoMapper;

import com.microservices.order_service.domain.model.OrderModel;
import com.microservices.order_service.domain.model.TrackModel;
import com.microservices.order_service.infrastructure.repository.entity.OrderEntity;
import com.microservices.order_service.infrastructure.repository.entity.OrderStatusHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderRepoMapper {
    OrderEntity toOrderEntity(OrderModel orderModel);
    OrderModel toOrderModel(OrderEntity orderEntity);
    TrackModel toTrackModel(OrderStatusHistoryEntity  statusHistory);
}

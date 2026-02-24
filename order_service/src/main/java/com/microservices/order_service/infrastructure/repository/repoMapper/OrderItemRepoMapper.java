package com.microservices.order_service.infrastructure.repository.repoMapper;

import com.microservices.order_service.domain.model.OrderItemModel;
import com.microservices.order_service.infrastructure.repository.entity.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemRepoMapper {
    OrderItemEntity toOrderItemEntity(OrderItemModel model);
    OrderItemModel toOrderItemModel(OrderItemEntity entity);
}

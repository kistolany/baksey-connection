package com.microservices.order_service.domain.mapper;

import com.microservices.order_service.application.request.OrderItemRequest;
import com.microservices.order_service.application.respone.OrderItemResponse;
import com.microservices.order_service.application.respone.OrderItemResponse;
import com.microservices.order_service.domain.model.OrderItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // Map the inner static class to the Model
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    OrderItemModel toModel(OrderItemRequest.OrderItem itemRequest);

    // Map the List automatically
    List<OrderItemModel> toModelList(List<OrderItemRequest.OrderItem> items);

    @Mapping(source = "models", target = "items")
    @Mapping(source = "id", target = "productId")
    OrderItemResponse toOrderItemResponse(List<OrderItemModel> models, UUID id);
}

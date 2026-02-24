package com.microservices.order_service.domain.mapper;

import com.microservices.order_service.application.request.OrderRequest;
import com.microservices.order_service.application.respone.OrderDetailResponse;
import com.microservices.order_service.application.respone.OrderResponse;
import com.microservices.order_service.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderModel toOrderModel(OrderRequest orderRequest);

    OrderResponse toOrderResponse(OrderModel orderModel);

    @Mapping(target = "deliveryAddress", ignore = true)
    OrderDetailResponse toOrderDetailResponse(OrderModel orderModel);
}

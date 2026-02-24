package com.microservices.order_service.domain.service;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.order_service.application.request.OrderRequest;
import com.microservices.order_service.application.respone.OrderDetailResponse;
import com.microservices.order_service.application.respone.OrderItemResponse;
import com.microservices.order_service.application.respone.OrderResponse;
import com.microservices.order_service.application.respone.SalesByProvinceResponse;
import com.microservices.order_service.domain.model.OrderModel;
import com.microservices.order_service.domain.model.TrackModel;

import java.util.List;
import java.util.UUID;


public interface OrderService {

    ResponseModel<OrderDetailResponse> getOrderById(UUID orderId);

    ResponseModel<List<OrderItemResponse>> listItemById(UUID orderId);

    ResponseModel<OrderResponse> createOrder(OrderRequest request);

    ResponseModel<PageResponse<OrderResponse>> listAllOrders(PageRequest request);

    ResponseModel<OrderResponse> confirmOrder(UUID orderId);

    ResponseModel<OrderResponse> shipOrder(UUID orderId);

    ResponseModel<OrderResponse> deliverOrder(UUID orderId);

    ResponseModel<List<TrackModel>> trackOrder(UUID orderId);

    ResponseModel<Void> cancelOrder(UUID orderId);

    ResponseModel<List<SalesByProvinceResponse>> getSalesByProvince();
}

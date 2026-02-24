package com.microservices.order_service.application;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.order_service.application.request.OrderRequest;
import com.microservices.order_service.application.respone.OrderDetailResponse;
import com.microservices.order_service.application.respone.OrderItemResponse;
import com.microservices.order_service.application.respone.OrderResponse;
import com.microservices.order_service.application.respone.SalesByProvinceResponse;
import com.microservices.order_service.domain.model.TrackModel;
import com.microservices.order_service.domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseModel<PageResponse<OrderResponse>> getAllOrders(@Valid PageRequest pageRequest) {
        log.info("GET: Get all orders");
        return orderService.listAllOrders(pageRequest);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<OrderDetailResponse> getOneOrder(@Valid @PathVariable UUID id) {
        log.info("GET: Get order with id: {}", id);
        return orderService.getOrderById(id);
    }

    @GetMapping(value = "/{id}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<OrderItemResponse>> getOrderItems(@PathVariable UUID id) {
        log.info("GET: Get order items by orderId: {}", id);
        return orderService.listItemById(id);
    }

    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<OrderResponse> saveOrder(@Valid @RequestBody OrderRequest request) {
        log.info("POST: Checkout order vie param: {}", CommonUtils.toJsonString(request));
        return orderService.createOrder(request);
    }

    @PutMapping("/{id}/confirm")
    public ResponseModel<OrderResponse> confirmOrder(@PathVariable UUID id) {
        log.info("PUT: Confirm orderId: {}", id);
        return orderService.confirmOrder(id);
    }

    @PutMapping("/{id}/ship")
    public ResponseModel<OrderResponse> shipOrder(@PathVariable UUID id) {
        log.info("PUT: Ship order: {}", id);
       return orderService.shipOrder(id);
    }

    @PutMapping("/{id}/deliver")
    public ResponseModel<OrderResponse> deliverOrder(@PathVariable UUID id) {
        log.info("PUT: Mark order as delivered: {}", id);
        return orderService.deliverOrder(id);
    }

    @GetMapping(value = "/{id}/track", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<TrackModel>> trackOrder(@Valid @PathVariable UUID id) {
        log.info("GET: Track order status by orderId {}", id);
        return orderService.trackOrder(id);
    }

    @DeleteMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Void> cancelOrder(@PathVariable UUID id) {
        log.info("DELETE: Cancel orderId {}", id);
        return orderService.cancelOrder(id);
    }

    @GetMapping(value = "/analytics/sales-by-province", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<SalesByProvinceResponse>> getSalesByProvince() {
        log.info("GET: Get sales summary by province");
        return orderService.getSalesByProvince();
    }

}

package com.microservices.payment_service.domain.outbound.rest;


import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.domain.outbound.request.StockRequest;
import com.microservices.product_service.domain.outbound.response.StockResponse;

public interface InventoryAdapter {
    ResponseModel<StockResponse> checkProductStock(StockRequest request);
}

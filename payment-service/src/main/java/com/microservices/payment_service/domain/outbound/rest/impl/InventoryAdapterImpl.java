package com.microservices.product_service.domain.outbound.rest.impl;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.domain.outbound.request.StockRequest;
import com.microservices.product_service.domain.outbound.response.StockResponse;
import com.microservices.product_service.domain.outbound.rest.InventoryAdapter;
import org.springframework.stereotype.Service;

@Service
public class InventoryAdapterImpl implements InventoryAdapter {
    @Override
    public ResponseModel<StockResponse> checkProductStock(StockRequest request) {

        // Logic here
        // .
        return ResponseModel.success(null);
    }
}

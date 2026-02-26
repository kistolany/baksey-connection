package com.microservices.product_service.domain.outbound.feignclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.domain.outbound.request.InventoryRequest;
import com.microservices.product_service.domain.outbound.response.InventoryResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.base.url}")
public interface InventoryFeignClient {

    @PutMapping("/add-stock")
    ResponseModel<InventoryResponse> addStock(@RequestBody InventoryRequest request);

    @PostMapping("/add-product") 
    ResponseModel<Void> addProduct(@RequestBody InventoryRequest request);

    @PostMapping("/bulk-stock")
    ResponseModel<List<InventoryResponse>> getBulkStock(@RequestBody List<UUID> productIds);
}
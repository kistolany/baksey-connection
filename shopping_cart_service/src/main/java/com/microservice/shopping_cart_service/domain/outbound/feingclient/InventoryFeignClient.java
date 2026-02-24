package com.microservice.shopping_cart_service.domain.outbound.feingclient;

import com.microservice.shopping_cart_service.domain.outbound.respone.InventoryResponse;
import com.microservices.common_service.domain.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.base.url}")
public interface InventoryFeignClient {

    @PostMapping("/bulk-stock")
    ResponseModel<List<InventoryResponse>> getBulkStock(@RequestBody List<String> productIds);
}
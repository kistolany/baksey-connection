package com.microservices.order_service.domain.outbound.feingclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.order_service.domain.outbound.request.InventoryRequest;
import com.microservices.order_service.domain.outbound.respone.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.base.url}")
public interface InventoryFeignClient {

    @PostMapping("/bulk-stock")
    ResponseModel<List<InventoryResponse>> getBulkStock(@RequestBody List<String> productIds);

    @PutMapping("/cut-stock")
    ResponseModel<Void> cutStock(@RequestBody InventoryRequest request);
}
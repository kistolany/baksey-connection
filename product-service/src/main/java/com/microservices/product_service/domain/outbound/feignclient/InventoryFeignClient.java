package com.microservices.product_service.domain.outbound.feignclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.domain.outbound.request.InventoryRequest;
import com.microservices.product_service.domain.outbound.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.base.url}")
public interface InventoryFeignClient {

    /*
    @PutMapping("${inventory.import.url}")
    ResponseModel<InventoryResponse> updateInventory(
            @RequestParam("inventoryId") UUID inventoryId,
            @RequestParam("productId") UUID productId,
            @RequestParam("quantity") Long quantity
    );
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseModel<PageResponse<InventoryResponse>> getAllInventory();
     */
    @PostMapping("/add-product") // Ensure this matches the @PostMapping in your Inventory Controller
    ResponseModel<Void> addProduct(@RequestBody InventoryRequest request);

    @PostMapping("/bulk-stock")
    ResponseModel<List<InventoryResponse>> getBulkStock(@RequestBody List<UUID> productIds);
}
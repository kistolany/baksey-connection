package com.microservices.inventory_service.application;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.inventory_service.domain.filter.FilterDTO;
import com.microservices.inventory_service.application.request.InventoryRequest;
import com.microservices.inventory_service.application.respone.InventoryResponse;
import com.microservices.inventory_service.domain.service.InventoryService;
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
@RequestMapping(value = "/api/v1/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/bulk-stock")
    public ResponseModel<List<InventoryResponse>> getBulkStock(@Valid @RequestBody List<UUID> productIds) {
        log.info("POST: Getting all inventory by product ids");
        return inventoryService.getStockByProductIds(productIds);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<PageResponse<InventoryResponse>> getAll(@Valid @ModelAttribute FilterDTO request,
            PageRequest pageRequest) {
        log.info("GET: Getting all inventory body");
        return inventoryService.getAll(request, pageRequest);
    }

    @GetMapping(value = "/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<InventoryResponse> getById(@Valid @PathVariable String id) {
        log.info("GET: Getting per product id");
        return inventoryService.getByProductId(id);
    }

    @PostMapping("/add-product")
    public ResponseModel<Void> addProduct(@Valid @RequestBody InventoryRequest request) {
        log.info("POST: Add new product to inventory : {}", CommonUtils.toJsonString(request));
        return inventoryService.addProduct(request);
    }

    @PutMapping("/add-stock")
    public ResponseModel<InventoryResponse> addStock(@Valid @RequestBody InventoryRequest request) {
        log.info("PUT: Add Quantity product to inventory : {}", CommonUtils.toJsonString(request));
        return inventoryService.addStock(request);
    }

    @PutMapping("/cut-stock")
    public ResponseModel<Void> cutStock(@Valid @RequestBody InventoryRequest request) {
        log.info("PUT: Cutting Stock from order : {}", CommonUtils.toJsonString(request));
        return inventoryService.cutStock(request);
    }

}

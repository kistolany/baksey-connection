package com.microservices.inventory_service.domain.service;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.inventory_service.domain.filter.FilterDTO;
import com.microservices.inventory_service.application.request.InventoryRequest;
import com.microservices.inventory_service.application.respone.InventoryResponse;
import java.util.List;
import java.util.UUID;

public interface InventoryService {

    // find stock by product id
    ResponseModel<List<InventoryResponse>> getStockByProductIds(List<UUID> productIds);

    // find by id
    ResponseModel<InventoryResponse> getByProductId(String productId);

    // add product
    ResponseModel<Void> addProduct(InventoryRequest request);

    // add stock
    ResponseModel<InventoryResponse> addStock(InventoryRequest request);

    // cut stock
    ResponseModel<Void> cutStock(InventoryRequest request);

    // list all
    ResponseModel<PageResponse<InventoryResponse>> getAll(FilterDTO filter, PageRequest pageRequest);
}

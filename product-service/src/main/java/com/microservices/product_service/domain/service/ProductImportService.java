package com.microservices.product_service.domain.service;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.ProductImportRequest;
import com.microservices.product_service.application.response.ProductImportResponse;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;

public interface ProductImportService {

    ResponseModel<ProductImportResponse> importProduct(ProductImportRequest request);

    ResponseModel<PageResponse<ProductImportResponse>> getImports(PageRequest pageRequest);

    ResponseModel<ProductImportResponse> getImportById(String id);

    ResponseModel<PageResponse<ProductImportResponse>> getImportsByProductId(String productId, PageRequest pageRequest);
}

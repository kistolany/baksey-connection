package com.microservices.product_service.domain.service;

import java.util.List;
import java.util.UUID;
import com.microservices.common_service.domain.*;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.constant.Constants.ProductStatus;
import com.microservices.product_service.domain.filter.ProductFilterRequest;

import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    ResponseModel<ProductResponse> create(ProductRequest request);

    ResponseModel<ProductResponse> getById(String id);

    ResponseModel<ProductResponse> update(ProductRequest productRequest, String id);

    ResponseModel<PageResponse<ProductResponse>> getAllByCategoryUuid(String categoryUuid, PageRequest pageRequest);

    ResponseModel<PageResponse<ProductResponse>> searchProducts(ProductFilterRequest request, PageRequest pageRequest);

    ResponseModel<List<ProductResponse>> listByProductIds(List<UUID> productIds);

    ResponseModel<List<String>> uploadImage(UUID productId, List<MultipartFile> files);

    ResponseModel<ProductResponse> updateStatus(String id, ProductStatus status);
}

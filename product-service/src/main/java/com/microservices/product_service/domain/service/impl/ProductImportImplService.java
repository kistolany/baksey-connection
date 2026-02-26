package com.microservices.product_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.product_service.application.request.ProductImportRequest;
import com.microservices.product_service.application.response.ProductImportResponse;
import com.microservices.product_service.domain.db_repo.ProductDomainRepo;
import com.microservices.product_service.domain.db_repo.ProductImportDomainRepo;
import com.microservices.product_service.domain.model.ProductImportModel;
import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.domain.outbound.feignclient.InventoryFeignClient;
import com.microservices.product_service.domain.outbound.request.InventoryRequest;
import com.microservices.product_service.domain.service.ProductImportService;
import com.microservices.product_service.domain.mapper.ProductImportMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProductImportImplService implements ProductImportService {

    private final ProductImportDomainRepo productImportDomainRepo;
    private final ProductDomainRepo productDomainRepo;
    private final InventoryFeignClient inventoryFeignClient;
    private final ProductImportMapper productImportMapper;

    @Override
    @Transactional
    public ResponseModel<ProductImportResponse> importProduct(ProductImportRequest request) {
        log.info("Start: Adding stock for Product ID: {}", request.getProductId());

        // 1. Validate quantity > 0
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            log.error("Add Stock Failed: Quantity must be greater than 0");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }

        // 2. Validate product exists
        ProductModel product = productDomainRepo.getById(request.getProductId().toString()).orElseThrow(() -> {
            log.error("Product id : {} is not found!", request.getProductId());
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Product id : " + request.getProductId() + " is not found!");
        });

        ProductImportModel savedModel = productImportDomainRepo.create(
                product.getId(),
                request.getQuantity(),
                request.getPricePerUnit(),
                request.getCurrency(),
                request.getDescription());

        // 4. Delegate to external inventory service using addStock
        InventoryRequest feignRequest = InventoryRequest.builder()
                .productId(product.getId())
                .availableStock(request.getQuantity())
                .build();
        inventoryFeignClient.addStock(feignRequest);

        // 5. Map to response
        ProductImportResponse response = productImportMapper.toResponse(savedModel);

        log.info("Finish: Added stock successfully for Product ID: {} via external inventory service", product.getId());
        return ResponseModel.success(response);
    }

    @Override
    public ResponseModel<PageResponse<ProductImportResponse>> getImports(PageRequest pageRequest) {
        log.info("Fetching all product imports");
        var page = productImportDomainRepo.getAll(pageRequest.toPageable());

        var responses = productImportMapper.toResponseList(page.getContent());

        return ResponseModel.success(PageResponse.fromPage(page, responses));
    }

    @Override
    public ResponseModel<ProductImportResponse> getImportById(String id) {
        log.info("Fetching product import with id: {}", id);
        var model = productImportDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Product import id : {} is not found!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Product import id : " + id + " is not found!");
        });

        ProductImportResponse response = productImportMapper.toResponse(model);
        return ResponseModel.success(response);
    }

    @Override
    public ResponseModel<PageResponse<ProductImportResponse>> getImportsByProductId(String productId,
            PageRequest pageRequest) {
        log.info("Fetching product imports for product id: {}", productId);
        var page = productImportDomainRepo.getByProductId(productId, pageRequest.toPageable());

        var responses = productImportMapper.toResponseList(page.getContent());

        return ResponseModel.success(PageResponse.fromPage(page, responses));
    }
}

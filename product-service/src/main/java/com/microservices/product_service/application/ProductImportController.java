package com.microservices.product_service.application;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.ProductImportRequest;
import com.microservices.product_service.application.response.ProductImportResponse;
import com.microservices.product_service.domain.service.ProductImportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/product-imports")
public class ProductImportController {

    private final ProductImportService inventoryService;

    @PostMapping
    public ResponseModel<ProductImportResponse> importStock(@RequestBody @Valid ProductImportRequest request) {
        log.info("POST: Import stock for Product ID: {}", request.getProductId());
        return inventoryService.importProduct(request);
    }

    @GetMapping
    public ResponseModel<PageResponse<ProductImportResponse>> getImports(PageRequest pageRequest) {
        log.info("GET: All product imports");
        return inventoryService.getImports(pageRequest);
    }

    @GetMapping("/{id}")
    public ResponseModel<ProductImportResponse> getImportById(@PathVariable String id) {
        log.info("GET: Product import by ID: {}", id);
        return inventoryService.getImportById(id);
    }

}

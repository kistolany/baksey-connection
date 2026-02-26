package com.microservices.product_service.application;

import java.util.List;
import java.util.UUID;
import com.microservices.common_service.domain.*;
import com.microservices.common_service.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.constant.Constants.ProductStatus;
import com.microservices.product_service.domain.filter.ProductFilterRequest;
import com.microservices.product_service.domain.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseModel<ProductResponse> createProduct(
            @RequestBody @Valid ProductRequest request) {
        log.info("POST: Create product with body");
        return productService.create(request);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProductResponse> getOneProduct(@Valid @PathVariable String id) {
        log.info("GET: Getting product with id : {}", id);
        return productService.getById(id);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProductResponse> updateProduct(@Valid @RequestBody ProductRequest request,
            @PathVariable String id) {
        log.info("PUT: Update product with id : {}", id);
        return productService.update(request, id);
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProductResponse> updateProductStatus(
            @PathVariable String id,
            @RequestParam ProductStatus status) {
        log.info("PATCH: Update status of product {} to {}", id, status);
        return productService.updateStatus(id, status);
    }

    @GetMapping
    public ResponseModel<PageResponse<ProductResponse>> getAllProduct(
            @ModelAttribute ProductFilterRequest searchRequest, PageRequest pageRequest) {
        // Log the incoming search parameters and request body
        log.info("GET: Search products with params: {} and request: {}", pageRequest,
                CommonUtils.toJsonString(searchRequest));
        return productService.searchProducts(searchRequest, pageRequest);
    }

    @PostMapping("/list-by-ids")
    public ResponseModel<List<ProductResponse>> getProductsByIds(@RequestBody List<UUID> ids) {
        log.info("POST: Fetching products for {} ids", ids);
        return productService.listByProductIds(ids);
    }

    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel<List<String>> uploadImage(
            @PathVariable UUID productId,
            @RequestPart("files") List<MultipartFile> files) {

        log.info("POST: Request to upload image for product: {}", productId);
        return productService.uploadImage(productId, files);
    }

    @GetMapping(value = "/category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<PageResponse<ProductResponse>> getAllByCategoryId(
            @Valid @PathVariable String id,
            @ModelAttribute PageRequest pageRequest) {
        log.info("GET: Getting paged products by category id: {}", id);
        return productService.getAllByCategoryUuid(id, pageRequest);
    }

}

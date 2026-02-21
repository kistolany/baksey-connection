package com.microservices.product_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.*;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductSpecification;
import com.microservices.product_service.domain.db_repo.BrandDomainRepo;
import com.microservices.product_service.domain.db_repo.CategoryDomainRepo;
import com.microservices.product_service.domain.outbound.feignclient.AttachmentClient;
import com.microservices.product_service.domain.outbound.request.InventoryRequest;
import com.microservices.product_service.domain.outbound.response.InventoryResponse;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.outbound.feignclient.InventoryFeignClient;
import com.microservices.product_service.domain.mapper.ProductMapper;
import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.domain.db_repo.ProductDomainRepo;
import com.microservices.product_service.domain.service.CategoryService;
import com.microservices.product_service.domain.service.ProductService;
import com.microservices.product_service.application.request.ProductSearchRequest;
import com.microservices.product_service.infrastructure.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@Service
public class ProductImplService implements ProductService {
    private final ProductMapper productMapper;
    private final ProductDomainRepo productDomainRepo;
    private final InventoryFeignClient inventoryFeignClient;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final CategoryDomainRepo categoryDomainRepo;
    private final BrandDomainRepo brandDomainRepo;
    private final AttachmentClient attachmentClient;

    @Override
    @Transactional
    public ResponseModel<ProductResponse> create(ProductRequest request) {
        log.info("Start: Create Product: {}, Category: {}", request.getProductName(), request.getCategoryId());

        // 2. Validate Category
        String categoryId = request.getCategoryId().toString();
        categoryDomainRepo.getById(categoryId).orElseThrow(() -> {
            log.error("Category id : {} is not found!", categoryId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Category id : " + categoryId + " is not found !");
        });

        // Validate Brand
        String brandId = request.getBrandId().toString();
        brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found!", brandId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Brand id : " + brandId + " is not found !");
        });

        // 3. Check for Duplicate
        boolean isDuplicate = productRepository.existsByProductNameAndCategoryUuidAndBrandUuid(
                request.getProductName(),
                UUID.fromString(request.getCategoryId().toString()),
                UUID.fromString(brandId)
        );

        if (isDuplicate) {
            log.error("Create Failed: Duplicate product {} found in this category/brand", request.getProductName());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Product with name %s already exists in this category and brand!", request.getProductName())
            );
        }

        // Call Repo: Just to save the data
        ProductModel savedProduct = productDomainRepo.create(
                request.getProductName(),
                request.getDescription(),
                request.getSalePrice(),
                categoryId,
                brandId

        );

        // Setting the request values
        InventoryRequest inventoryReq = InventoryRequest.builder()
                .productId(savedProduct.getId())
                .availableStock(0)
                .build();

        // Send it via Feign
        inventoryFeignClient.addProduct(inventoryReq);

        log.info("Finish: Create Product with successfully !");
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<ProductResponse> getById(String id) {
        log.info("Start: Getting Product by Id : {}", id);

        // Validate product id
        ProductModel productModel = productDomainRepo.getById(id).orElseThrow(() -> {
            log.info("Product id : {} is not found !", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Product is not found !");
        });

        // map model to response
        ProductResponse productResponse = productMapper.toProductResponse(productModel);

        // Map to get quantity product id from inventory
        Map<UUID, Integer> stockMap = fetchStock(List.of(productModel.getId()));

        // set quantity to model
        productModel.setQuantity(stockMap.getOrDefault(productModel.getId(), 0));

        // call for response brand and category name
        enrichProductDetails(productResponse);

        // return product response
        log.info("Finish: Getting Product by id {} is successfully ", id);
        return ResponseModel.success(productResponse);
    }

    @Override
    public ResponseModel<ProductResponse> update(ProductRequest productRequest, String id) {
        log.info("Start: Updating Product ID: {} with Body: {}", id, CommonUtils.toJsonString(productRequest));

        // 1. Check if Product exists
        ProductModel product = productDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Product id : {} is not found !", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Product id : " + id + " is not found !");
        });

        // 2. Validate Category
        String categoryId = productRequest.getCategoryId().toString();
        categoryDomainRepo.getById(categoryId).orElseThrow(() -> {
            log.error("Category id : {} is not found !", categoryId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Category id : " + categoryId + " is not found !");
        });

        // Validate Brand
        String brandId = productRequest.getBrandId().toString();
        brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found !", brandId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Brand id : " + brandId + " is not found !");
        });

        // 3. Check for Duplicate
        boolean isSameName = product.getProductName().equalsIgnoreCase(productRequest.getProductName());
        boolean isSameCategory = product.getCategoryId().equals(productRequest.getCategoryId().toString());
        boolean isSameBrand = product.getBrandId().equals(productRequest.getBrandId().toString());

        if (isSameName && isSameCategory && isSameBrand) {
            log.error("Update Failed: Product name {}, Category, and Brand are unchanged!", productRequest.getProductName());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Product with name %s already exists in this category and brand!", productRequest.getProductName())
            );
        }

        // 4. Call Repo: update the fields and save
        productDomainRepo.update(
                product,
                productRequest.getProductName(),
                productRequest.getDescription(),
                productRequest.getSalePrice(),
                categoryId,
                brandId,
                id);

        log.info("Finish: Updating Product ID: {} Successfully !", id);
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<PageResponse<ProductResponse>> getAllByCategoryUuid(String categoryUuid, PageRequest pageRequest) {
        log.info("Start: Getting paged products by Category id: {}", categoryUuid);

        // Validate category existence
        categoryService.getById(categoryUuid);

        // Call Repo with Pageable
        Page<ProductModel> pageModel = productDomainRepo.getAllByCategoryUuid(categoryUuid, pageRequest.toPageable());

        // Extract the data content from the pagination to return list
        List<ProductModel> listProduct = pageModel.getContent();

        // Call fetchStock for map to get product id
        Map<UUID, Integer> stockMap = fetchStock(listProduct.stream().map(ProductModel::getId).toList());

        listProduct.forEach(productModel -> {

            // Get the current stock from the map
            Integer stock = stockMap.getOrDefault(productModel.getId(), 0);
            productModel.setQuantity(stock);
        });

        // map model to response and collect to list
        List<ProductResponse> listResponse = pageModel.getContent().stream().map(productMapper::toProductResponse).collect(Collectors.toList());

        // Return a new PageResponse using the mapped list and the original pagination
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(pageModel, listResponse);

        log.info("Finish: Getting paged products by category with successfully !");
        return ResponseModel.success(pageResponse);
    }

    @Override
    public ResponseModel<PageResponse<ProductResponse>> searchProducts(ProductSearchRequest request, PageRequest pageRequest) {
        log.info("Start: Searching products with request: {}", CommonUtils.toJsonString(request));

        // 1. Create Search Specification
        ProductSpecification spec = new ProductSpecification(request);

        // 2. Call Repo
        Page<ProductModel> page = productDomainRepo.searchProducts(spec, pageRequest.toPageable());
        List<ProductModel> products = page.getContent();

        // 3. Fetch stock data in bulk
        Map<UUID, Integer> stockMap = fetchStock(products.stream().map(ProductModel::getId).toList());

        // 4. Map, Enrich, and Set Stock in one flow
        List<ProductResponse> enrichedList = products.stream()
                .map(productModel -> {
                    // Map to Response DTO
                    ProductResponse resp = productMapper.toProductResponse(productModel);

                    // Set stock from our map
                    resp.setQuantity(stockMap.getOrDefault(productModel.getId(), 0));

                    // Call your private method to add Brand and Category names
                    this.enrichProductDetails(resp);

                    return resp;
                })
                .toList();

        log.info("Finish: Found {} products successfully!", enrichedList.size());

        // 5. Return the ENRICHED list
        return ResponseModel.success(PageResponse.fromPage(page, enrichedList));
    }

    @Override
    public ResponseModel<List<ProductResponse>> listByProductIds(List<UUID> productIds) {
        log.info("Start: Batch Getting products for IDs: {}", productIds);

        // 1. Fetch from Domain Repo
        List<ProductModel> models = productDomainRepo.findProductIds(productIds);

        // 2. Handle empty case
        if (models.isEmpty()) {
            log.warn("Finish: No products found for provided IDs");
            return ResponseModel.success(Collections.emptyList());
        }

        // 3. Getting Stock from Inventory
        Map<UUID, Integer> stockMap = fetchStock(models.stream().map(ProductModel::getId).toList());

        // 4. Map, Enrich Names, and Merge Stock
        List<ProductResponse> list = models.stream()
                .map(model -> {
                    ProductResponse response = productMapper.toProductResponse(model);

                    // SET STOCK
                    response.setQuantity(stockMap.getOrDefault(model.getId(), 0));

                    // FIX: CALL ENRICHMENT TO GET BRAND AND CATEGORY NAMES
                    this.enrichProductDetails(response);

                    return response;
                })
                .toList();

        log.info("Finish: Successfully batch fetched {} products enriched with names", list.size());
        return ResponseModel.success(list);
    }


    @Override
    @Transactional
    public ResponseModel<String> uploadImage(UUID productId, MultipartFile file) {
        log.info("Start: Uploading image for product ID: {}", productId);

        // Validate product exists
        ProductModel product = productDomainRepo.getById(productId.toString())
                .orElseThrow(() -> {
                    log.error("Product id {} not found", productId);
                    return new ApiException(
                            ResponseConstants.ResponseStatus.NOT_FOUND,
                            "Product not found"
                    );
                });

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Image file must not be empty"
            );
        }

        // Keep old image id (important)
        String oldImageId = product.getImagePath();

        // Upload new image
        ResponseModel<String> attachmentResponse =
                attachmentClient.uploadImage("products", file);

        if (attachmentResponse == null || attachmentResponse.getData() == null) {
            log.error("Image upload failed");
            throw new ApiException("Image upload failed");
        }

        String newImageId = attachmentResponse.getData();

        // Update DB with new image id
        productDomainRepo.updateProductImage(productId, newImageId);

        // delete old image
        if (oldImageId != null) {
            try {
                attachmentClient.deleteImage("products", oldImageId);
            } catch (Exception e) {
                log.warn("Failed to delete old image: {}", oldImageId);
            }
        }

        log.info("Finish: Image upload and DB update successful");
        return ResponseModel.success(newImageId);
    }

    //Helper
    private Map<UUID, Integer> fetchStock(List<UUID> ids) {
        try {

            // call inventory for check available stock
            var resp = inventoryFeignClient.getBulkStock(ids);

            // check if response not null
            return (resp != null && resp.getData() != null) ? resp.getData().stream()

                    // nap to get key product and value available
                    .collect(Collectors.toMap(InventoryResponse::getProductId, InventoryResponse::getAvailableStock, (a, b) -> a))
                    : Collections.emptyMap();
        } catch (Exception e) {

            // if null return empty
            return Collections.emptyMap();
        }
    }

    // brand and category
    private void enrichProductDetails(ProductResponse response) {
        if (response == null) return;

        // Fetch and set Category Name
        if (response.getCategoryId() != null) {
            categoryDomainRepo.getById(response.getCategoryId().toString())
                    .ifPresent(category -> response.setCategoryName(category.getCategoryName()));
        }

        // Fetch and set Brand Name
        if (response.getBrandId() != null) {
            brandDomainRepo.getById(response.getBrandId().toString())
                    .ifPresent(brand -> response.setBrandName(brand.getBrandName()));
        }
    }

}

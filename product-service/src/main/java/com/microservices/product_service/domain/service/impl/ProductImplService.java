package com.microservices.product_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.*;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.BrandResponse;
import com.microservices.product_service.application.response.CategoryResponse;
import com.microservices.product_service.domain.db_repo.BrandDomainRepo;
import com.microservices.product_service.domain.db_repo.CategoryDomainRepo;
import com.microservices.product_service.domain.outbound.feignclient.AttachmentClient;
import com.microservices.product_service.domain.outbound.request.InventoryRequest;
import com.microservices.product_service.domain.outbound.response.InventoryResponse;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.outbound.feignclient.InventoryFeignClient;
import com.microservices.product_service.domain.mapper.ProductMapper;
import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.domain.constant.Constants.ProductStatus;
import com.microservices.product_service.domain.db_repo.ProductDomainRepo;
import com.microservices.product_service.domain.filter.ProductFilterRequest;
import com.microservices.product_service.domain.filter.ProductFilterSpecification;
import com.microservices.product_service.domain.service.CategoryService;
import com.microservices.product_service.domain.service.ProductService;
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
        log.info("Start: Create Product: {}, Category: {}", request.getName(), request.getCategoryId());

        // 2. Validate Category
        String categoryId = request.getCategoryId().toString();
        categoryDomainRepo.getById(categoryId).orElseThrow(() -> {
            log.error("Category id : {} is not found!", categoryId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Category id : " + categoryId + " is not found !");
        });

        // Validate Brand
        String brandId = request.getBrandId().toString();
        brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found!", brandId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Brand id : " + brandId + " is not found !");
        });

        // 3. Check for Duplicate
        boolean isDuplicate = productRepository.existsByNameAndCategoryUuidAndBrandUuid(
                request.getName(),
                UUID.fromString(request.getCategoryId().toString()),
                UUID.fromString(brandId));

        if (isDuplicate) {
            log.error("Create Failed: Duplicate product {} found in this category/brand", request.getName());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Product with name %s already exists in this category and brand!",
                            request.getName()));
        }

        // Call Repo: Just to save the data
        ProductModel savedProduct = productDomainRepo.create(
                request.getName(),
                request.getDescription(),
                request.getSalePrice(),
                categoryId,
                brandId,
                request.getStatus());

        // Setting the request values for inventory
        InventoryRequest inventoryReq = InventoryRequest.builder()
                .productId(savedProduct.getId())
                .availableStock(0)
                .build();

        // Send it via Feign
        inventoryFeignClient.addProduct(inventoryReq);

        // Map to response and enrich with details
        ProductResponse productResponse = productMapper.toProductResponse(savedProduct);

        // Set initial stock as 0 (just created)
        productResponse.setQuantity(0);

        // Enrich with brand and category names
        enrichProductDetails(productResponse, savedProduct);

        log.info("Finish: Create Product with successfully !");
        return ResponseModel.success(productResponse);
    }

    @Override
    public ResponseModel<ProductResponse> getById(String id) {

        log.info("Start: Getting Product by Id : {}", id);

        ProductModel productModel = productDomainRepo.getById(id)
                .orElseThrow(() -> new ApiException(
                        ResponseConstants.ResponseStatus.NOT_FOUND,
                        "Product is not found !"));

        ProductResponse productResponse = productMapper.toProductResponse(productModel);

        Map<UUID, Integer> stockMap = fetchStock(List.of(productModel.getId()));

        // ✅ FIX: set quantity to response, NOT model
        productResponse.setQuantity(
                stockMap.getOrDefault(productModel.getId(), 0));

        enrichProductDetails(productResponse, productModel);

        log.info("Finish: Getting Product by id {} successfully", id);
        return ResponseModel.success(productResponse);
    }

    @Override
    public ResponseModel<ProductResponse> update(ProductRequest productRequest, String id) {
        log.info("Start: Updating Product ID: {} with Body: {}", id, CommonUtils.toJsonString(productRequest));

        // 1. Check if Product exists
        ProductModel product = productDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Product id : {} is not found !", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Product id : " + id + " is not found !");
        });

        // 2. Validate Category
        String categoryId = productRequest.getCategoryId().toString();
        categoryDomainRepo.getById(categoryId).orElseThrow(() -> {
            log.error("Category id : {} is not found !", categoryId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Category id : " + categoryId + " is not found !");
        });

        // Validate Brand
        String brandId = productRequest.getBrandId().toString();
        brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found !", brandId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Brand id : " + brandId + " is not found !");
        });

        // 3. Check for Duplicate
        boolean isSameName = product.getName().equalsIgnoreCase(productRequest.getName());
        boolean isSameCategory = product.getCategoryId().equals(productRequest.getCategoryId());
        boolean isSameBrand = product.getBrandId().equals(productRequest.getBrandId());

        if (isSameName && isSameCategory && isSameBrand) {
            log.error("Update Failed: Product name {}, Category, and Brand are unchanged!", productRequest.getName());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Product with name %s already exists in this category and brand!",
                            productRequest.getName()));
        }

        // 4. Call Repo: update the fields and save
        ProductModel updatedProduct = productDomainRepo.update(
                product,
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getSalePrice(),
                categoryId,
                brandId,
                productRequest.getStatus(),
                id);

        // Map to response and enrich with details
        ProductResponse productResponse = productMapper.toProductResponse(updatedProduct);

        // Fetch and set stock from inventory
        Map<UUID, Integer> stockMap = fetchStock(List.of(updatedProduct.getId()));
        productResponse.setQuantity(stockMap.getOrDefault(updatedProduct.getId(), 0));

        // Enrich with brand and category names
        enrichProductDetails(productResponse, updatedProduct);

        log.info("Finish: Updating Product ID: {} Successfully !", id);
        return ResponseModel.success(productResponse);
    }

    @Override
    public ResponseModel<PageResponse<ProductResponse>> getAllByCategoryUuid(String categoryUuid,
            PageRequest pageRequest) {
        log.info("Start: Getting paged products by Category id: {}", categoryUuid);

        // 1. Validate category existence
        categoryService.getById(categoryUuid);

        // 2. Call Repo with Pageable
        Page<ProductModel> pageModel = productDomainRepo.getAllByCategoryUuid(categoryUuid, pageRequest.toPageable());
        List<ProductModel> listProduct = pageModel.getContent();

        // 3. Fetch stock data in bulk
        Map<UUID, Integer> stockMap = fetchStock(listProduct.stream().map(ProductModel::getId).toList());

        // 4. Map to Response, Set Stock, and Enrich Details
        List<ProductResponse> listResponse = listProduct.stream()
                .map(productModel -> {
                    // Map Model to DTO
                    ProductResponse resp = productMapper.toProductResponse(productModel);

                    // Set the current stock from the map
                    resp.setQuantity(stockMap.getOrDefault(productModel.getId(), 0));

                    // THE MISSING PIECE: Populate Brand and Category Names
                    this.enrichProductDetails(resp, productModel);

                    return resp;
                })
                .collect(Collectors.toList());

        // 5. Return the PageResponse
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(pageModel, listResponse);

        log.info("Finish: Getting paged products by category successfully!");
        return ResponseModel.success(pageResponse);
    }

    @Override
    public ResponseModel<PageResponse<ProductResponse>> searchProducts(ProductFilterRequest request,
            PageRequest pageRequest) {
        log.info("Start: Searching products with request: {}", CommonUtils.toJsonString(request));

        // 1. Create Search Specification
        ProductFilterSpecification spec = new ProductFilterSpecification(request);

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
                    this.enrichProductDetails(resp, productModel);

                    return resp;
                })
                .toList();

        log.info("Finish: Found {} products successfully!", enrichedList.size());

        // 5. Return the ENRICHED list
        return ResponseModel.success(PageResponse.fromPage(page, enrichedList));
    }

    @Override
    public ResponseModel<List<ProductResponse>> listByProductIds(List<UUID> productIds) {
        log.info("Start: Batch Getting products for IDs: {}", CommonUtils.toJsonString(productIds));

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
                    this.enrichProductDetails(response, model);

                    return response;
                })
                .toList();

        log.info("Finish: Successfully batch fetched {} products enriched with names", CommonUtils.toJsonString(list));
        return ResponseModel.success(list);
    }

    @Override
    @Transactional
    public ResponseModel<List<String>> uploadImage(UUID productId, List<MultipartFile> file) {
        // 1. Upload file to storage service
        List<String> newImageIds = attachmentClient.uploadImage("products", file).getData();

        // 2. Fetch current product
        ProductModel product = productDomainRepo.getById(productId.toString())
                .orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Product not found"));

        // 3. Update the list (Append mode)
        List<String> currentImages = product.getImages();
        if (currentImages == null)
            currentImages = new ArrayList<>();
        currentImages.addAll(newImageIds);

        // 4. Save back to DB
        productDomainRepo.updateProductImage(productId, currentImages);

        return ResponseModel.success(newImageIds);
    }

    @Override
    @Transactional
    public ResponseModel<ProductResponse> updateStatus(String id, ProductStatus status) {
        log.info("Start: Updating status of Product ID: {} to {}", id, status);

        // 1. Validate product exists
        productDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Product id : {} is not found!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    "Product id : " + id + " is not found!");
        });

        // 2. Update status
        ProductModel updatedProduct = productDomainRepo.updateStatus(id, status);

        // 3. Map to response
        ProductResponse productResponse = productMapper.toProductResponse(updatedProduct);

        // 4. Fetch and set stock
        Map<UUID, Integer> stockMap = fetchStock(List.of(updatedProduct.getId()));
        productResponse.setQuantity(stockMap.getOrDefault(updatedProduct.getId(), 0));

        // 5. Enrich with brand and category names
        enrichProductDetails(productResponse, updatedProduct);

        log.info("Finish: Updated status of Product ID: {} to {} successfully!", id, status);
        return ResponseModel.success(productResponse);
    }

    // Helper
    private Map<UUID, Integer> fetchStock(List<UUID> ids) {
        try {

            // call inventory for check available stock
            var resp = inventoryFeignClient.getBulkStock(ids);

            // check if response not null
            return (resp != null && resp.getData() != null) ? resp.getData().stream()

                    // nap to get key product and value available
                    .collect(Collectors.toMap(InventoryResponse::getProductId, InventoryResponse::getAvailableStock,
                            (a, b) -> a))
                    : Collections.emptyMap();
        } catch (Exception e) {

            // if null return empty
            return Collections.emptyMap();
        }
    }

    // brand and category
    private void enrichProductDetails(ProductResponse response, ProductModel model) {
        if (response == null || model == null)
            return;

        // Use the ID from the MODEL, not the response
        if (model.getCategoryId() != null) {
            categoryDomainRepo.getById(model.getCategoryId().toString())
                    .ifPresent(entity -> {
                        response.setCategory(CategoryResponse.builder()
                                .uuid(entity.getUuid())
                                .name(entity.getName())
                                .imageUrl(entity.getImageUrl())
                                .createdAt(entity.getCreatedAt())
                                .lastUpdatedAt(entity.getLastUpdatedAt())
                                .build());
                    });
        }

        if (model.getBrandId() != null) {
            brandDomainRepo.getById(model.getBrandId().toString())
                    .ifPresent(entity -> {
                        response.setBrand(BrandResponse.builder()
                                .uuid(entity.getUuid())
                                .name(entity.getName())
                                .imageUrl(entity.getImageUrl())
                                .createdAt(entity.getCreatedAt())
                                .lastUpdatedAt(entity.getLastUpdatedAt())
                                .build());
                    });
        }
    }

}

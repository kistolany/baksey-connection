package com.microservices.inventory_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.inventory_service.domain.filter.FilterDTO;
import com.microservices.inventory_service.application.request.InventoryRequest;
import com.microservices.inventory_service.application.respone.InventoryResponse;
import com.microservices.inventory_service.domain.db_repo.InventoryDomainRepo;
import com.microservices.inventory_service.domain.mapper.InventoryMapper;
import com.microservices.inventory_service.domain.model.InventoryModel;
import com.microservices.inventory_service.domain.outbound.feignclient.ProductFeignClient;
import com.microservices.inventory_service.domain.outbound.response.ProductResponse;
import com.microservices.inventory_service.domain.service.InventoryService;
import com.microservices.inventory_service.infrastructure.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryDomainRepo inventoryRepoService;
    private final InventoryMapper inventoryMapper;
    private final ProductFeignClient productFeignClient;
    private final InventoryRepository inventoryRepository;

    @Override
    public ResponseModel<List<InventoryResponse>> getStockByProductIds(List<UUID> productIds) {
        log.info("Start: getting inventory by product id : {} from product service", productIds);

        // check stock by product ids
        List<InventoryModel> models = inventoryRepoService.findStockByProductIds(productIds);

        // map model to respone and return
        List<InventoryResponse> list = models.stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();

        // return list respone class
        log.info("Finish: getting inventory by product Id {} from product service",productIds);
        return ResponseModel.success(list);
    }

    @Override
    public ResponseModel<InventoryResponse> getByProductId(String productId) {
        log.info("Start: Getting Product by ID :{}", productId);

        // Call getById from repo
        InventoryModel inventoryModel = inventoryRepoService.getById(productId).orElseThrow(() -> {

            // log message when not found of id
            log.error("Product is not found! with ID: {} ", productId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Product not found! with ID: %s ", productId));
        });

        // Call method validate for exist product
        ResponseModel<ProductResponse> productResponse = productFeignClient.getProductById(UUID.fromString(productId));

        // validate if product respone is null
        if (productResponse.getData() == null) {
            log.error("Fetch Product data is fail!");
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,"Fetch Product data is fail!");
        }

        // validate if product existing
        if (productResponse.getData().getProductId() == null) {
            log.error("Product  " + productId + " is not found!");
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,"Product ID " + productId + " not found!");
        }

        // create object product response
        ProductResponse ProductData = productResponse.getData();

        // set product name to inventory model
        inventoryModel.setProductName(ProductData.getName());

        // map entity ro respone
        InventoryResponse inventoryResponse = inventoryMapper.toInventoryResponse(inventoryModel);
        inventoryResponse.setBrandName(ProductData.getBrandName());
        inventoryResponse.setCategoryName(ProductData.getCategoryName());

        log.info("Finish: Getting Product by id");
        return ResponseModel.success(inventoryResponse);
    }

    @Override
    @Transactional
    public ResponseModel<Void> addProduct(InventoryRequest request) {
        log.info("Start: Add product to Inventory :{}", request.getProductId());

        // STEP 2: Use the ID directly from the request
        UUID productId = request.getProductId();

        // STEP 3: Check if this product is already in Inventory
        boolean alreadyInInventory = inventoryRepository.existsByProductId(productId);

        if (alreadyInInventory) {
            log.error("Product {} already exists in Inventory", productId);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Product is already exist in inventory!");
        }

        // STEP 4: Save to database
        inventoryRepoService.addProduct(productId.toString());

        log.info("Finish: Add product to Inventory :{}", productId);
        return ResponseModel.success();
    }

    @Override
    @Transactional
    public ResponseModel<InventoryResponse> addStock(InventoryRequest request) {

        log.info("Start: Processing stock addition for product: {}",
                CommonUtils.toJsonString(request));

        // 1️⃣ Get current inventory
        ResponseModel<InventoryResponse> currentInventory =
                getByProductId(request.getProductId().toString());

        Integer currentAvailable =
                currentInventory.getData().getAvailableStock() == null
                        ? 0
                        : currentInventory.getData().getAvailableStock();

        Integer incomingStock =
                request.getAvailableStock() == null
                        ? 0
                        : request.getAvailableStock();

        Integer newTotal = currentAvailable + incomingStock;

        // 2️⃣ Save new stock
        InventoryModel inventoryModel =
                inventoryRepoService.addStock(
                        request.getProductId().toString(),
                        newTotal
                );

        // 3️⃣ Map inventory response
        InventoryResponse response =
                inventoryMapper.toInventoryResponse(inventoryModel);

        // 4️⃣ 🔥 Enrich product data before return
        ResponseModel<ProductResponse> productResponse =
                productFeignClient.getProductById(request.getProductId());

        if (productResponse.getData() != null) {

            ProductResponse product = productResponse.getData();

            response.setProductName(product.getName());
            response.setBrandName(product.getBrandName());
            response.setCategoryName(product.getCategoryName());
            response.setImagePath(product.getImages());
            response.setSalePrice(product.getSalePrice());
        }

        log.info("Finish: Updated product {} stock from {} to {}",
                request.getProductId(), currentAvailable, newTotal);

        return ResponseModel.success(response);
    }


    @Override
    @Transactional
    public ResponseModel<Void> cutStock(InventoryRequest request) {
        log.info("Start: Cutting stock for Product: {}", CommonUtils.toJsonString(request));

        // 1.  Check existence and throw exception if missing
        ResponseModel<InventoryResponse> current = getByProductId(request.getProductId().toString());

        // 2.  Calculation and Availability check
        int availableStock = current.getData().getAvailableStock() == null ? 0 : current.getData().getAvailableStock();

        // validate if request greater than available stock
        if (availableStock < request.getAvailableStock()) {
            log.error("Product stock is not enough!");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Product stock is not enough!");
        }

        // validate if  product = 0 stock
        if (availableStock == 0) {
            log.error("Product out of stock!");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Product out of stock!");
        }

        // Minus stock when order is processing
        Integer newTotal = availableStock - request.getAvailableStock();

        // 3. Call repo to execute the save
        inventoryRepoService.cutStock(request.getProductId().toString(), newTotal);
        log.info("Finish: Stock cut successfully for Product: {}", request.getProductId());
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<PageResponse<InventoryResponse>> getAll(FilterDTO filter, PageRequest pageRequest) {
        log.info("Start: Search Inventory with Filter: {}", CommonUtils.toJsonString(filter));

        // 1. Fetch from DB (Min/Max Stock filters applied at DB level)
        Page<InventoryModel> modelPage = inventoryRepoService.listAll(filter, null, pageRequest.toPageable());

        if (modelPage.isEmpty()) {
            return ResponseModel.success(PageResponse.fromPage(modelPage, List.of()));
        }

        // 2. Batch fetch product details from Product Service
        List<UUID> productIds = modelPage.getContent().stream()
                .map(InventoryModel::getProductId).toList();


        // Call product beingClient
        ResponseModel<List<ProductResponse>> productData = productFeignClient.getProductsByIds(productIds);

        // Create lookup map for enrichment
        Map<String, ProductResponse> productMap = (productData.getData() == null) ? Map.of() :
                productData.getData().stream().collect(Collectors.toMap(
                        ProductResponse::getProductId, p -> p));

        // 3. Combine Mapping, Enrichment, and Name Filtering in one Stream
        List<InventoryResponse> results = modelPage.getContent().stream()
                .map(model -> {
                    InventoryResponse resp = inventoryMapper.toInventoryResponse(model);
                    ProductResponse detail = productMap.get(model.getProductId().toString());

                    if (detail != null) {
                        resp.setProductName(detail.getName());
                        resp.setBrandName(detail.getBrandName());
                        resp.setCategoryName(detail.getCategoryName());
                        resp.setImagePath(detail.getImages());
                        resp.setSalePrice(detail.getSalePrice());
                    }
                    return resp;
                })
                .filter(resp -> {
                    // COMBINED FILTER LOGIC
                    boolean matches = true;

                    if (filter.getProductName() != null && !filter.getProductName().isBlank()) {
                        matches &= resp.getProductName() != null &&
                                resp.getProductName().toLowerCase().contains(filter.getProductName().toLowerCase());
                    }
                    if (filter.getBrandName() != null && !filter.getBrandName().isBlank()) {
                        matches &= resp.getBrandName() != null &&
                                resp.getBrandName().equalsIgnoreCase(filter.getBrandName());
                    }
                    if (filter.getCategoryName() != null && !filter.getCategoryName().isBlank()) {
                        matches &= resp.getCategoryName() != null &&
                                resp.getCategoryName().equalsIgnoreCase(filter.getCategoryName());
                    }
                    return matches;
                })
                .toList();

        log.info("Finish: Found {} items after in-memory filtering", results.size());
        return ResponseModel.success(PageResponse.fromPage(modelPage, results));
    }

}

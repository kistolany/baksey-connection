package com.microservice.shopping_cart_service.domain.service.impl;

import com.microservice.shopping_cart_service.application.request.CartRequest;
import com.microservice.shopping_cart_service.application.respone.CartResponse;
import com.microservice.shopping_cart_service.domain.constant.Constants.CartStatusEnum;
import com.microservice.shopping_cart_service.domain.db_repo.CartDomainRepo;
import com.microservice.shopping_cart_service.domain.mapper.CartMapper;
import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import com.microservice.shopping_cart_service.domain.outbound.feingclient.InventoryFeignClient;
import com.microservice.shopping_cart_service.domain.outbound.feingclient.ProductFeignClient;
import com.microservice.shopping_cart_service.domain.outbound.respone.InventoryResponse;
import com.microservice.shopping_cart_service.domain.outbound.respone.ProductResponse;
import com.microservice.shopping_cart_service.domain.service.CartService;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.microservice.shopping_cart_service.domain.constant.Constants.CartStatusEnum.ACTIVE;

@Slf4j
@AllArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartDomainRepo cartDomainRepo;
    private final CartMapper cartMapper;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;

    @Override
    @Transactional
    public ResponseModel<CartResponse> createOrUpdateCart(
            String userId,
            List<CartRequest> requests
    ) {

        log.info("START: Create Cart and item by user ID: {} with request body {}",
                userId, CommonUtils.toJsonString(requests));

        UUID userUuid = UUID.fromString(userId);

        // 1️⃣ Extract Product IDs
        List<String> productIds = requests.stream()
                .flatMap(req -> req.getItems().stream())
                .map(item -> item.getProductId().toString())
                .distinct()
                .toList();

        if (productIds.isEmpty()) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Cart request is empty"
            );
        }

        // 2️⃣ Call Product Service (ONLY ONCE)
        ResponseModel<List<ProductResponse>> productResponse =
                productFeignClient.getProductsByIds(productIds);

        List<ProductResponse> productData = productResponse.getData();

        if (productData == null || productData.isEmpty()) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Products not found"
            );
        }

        // 3️⃣ Call Inventory Service (ONLY ONCE)
        ResponseModel<List<InventoryResponse>> inventoryResponse =
                inventoryFeignClient.getBulkStock(productIds);

        List<InventoryResponse> inventoryData = inventoryResponse.getData();

        // 4️⃣ Build Maps
        Map<UUID, ProductResponse> productMap =
                productData.stream()
                        .collect(Collectors.toMap(
                                p -> UUID.fromString(p.getProductId()),
                                p -> p
                        ));

        Map<UUID, Integer> stockMap =
                inventoryData.stream()
                        .collect(Collectors.toMap(
                                InventoryResponse::getProductId,
                                InventoryResponse::getAvailableStock
                        ));

        // 5️⃣ VALIDATION
        for (CartRequest request : requests) {
            for (var item : request.getItems()) {

                UUID pid = item.getProductId();

                if (item.getQuantity() <= 0) {
                    throw new ApiException(
                            ResponseConstants.ResponseStatus.BAD_REQUEST,
                            "Quantity must be at least 1"
                    );
                }

                if (!productMap.containsKey(pid)) {
                    throw new ApiException(
                            ResponseConstants.ResponseStatus.BAD_REQUEST,
                            "Product not found: " + pid
                    );
                }

                Integer availableStock = stockMap.getOrDefault(pid, 0);

                if (availableStock < item.getQuantity()) {
                    throw new ApiException(
                            ResponseConstants.ResponseStatus.BAD_REQUEST,
                            "Stock not enough for product: " + pid
                    );
                }
            }
        }

        // 6️⃣ Convert Request → Model
        List<CartItemModel> itemsToProcess =
                requests.stream()
                        .flatMap(req -> req.getItems().stream())
                        .map(cartMapper::toItemModel)
                        .toList();

        // 7️⃣ Build Price Map
        Map<UUID, BigDecimal> priceMap =
                productMap.values().stream()
                        .collect(Collectors.toMap(
                                p -> UUID.fromString(p.getProductId()),
                                ProductResponse::getSalePrice
                        ));

        // 8️⃣ Save Cart
        cartDomainRepo.createOrUpdateCart(
                userUuid,
                itemsToProcess,
                priceMap
        );

        // 9️⃣ Fetch Updated Cart
        CartModel updatedCart =
                cartDomainRepo.getItemByUserId(userUuid)
                        .orElseThrow(() ->
                                new ApiException(
                                        ResponseConstants.ResponseStatus.NOT_FOUND,
                                        "Cart not found"
                                )
                        );

        // 🔟 Enrich Product Info (NO EXTRA API CALL)
        updatedCart.getItems().forEach(item -> {

            ProductResponse product =
                    productMap.get(item.getProductId());

            if (product != null) {
                item.setProductName(product.getProductName());
                item.setProductImage(product.getImagePath());
            }
        });

        log.info("FINISH: Cart created/updated successfully for user: {}", userId);

        return ResponseModel.success(
                cartMapper.toCartResponse(updatedCart)
        );
    }
    @Override
    public ResponseModel<CartResponse> getCartByUserId(String userId) {
        log.info("START: Getting Cart and item by user ID: {}", userId);

        // 1. Retrieve the lean data from our Cart DB
        CartModel cartModel = cartDomainRepo.getItemByUserId(UUID.fromString(userId)).orElseThrow(() -> {
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "No active cart found");
        });

        // 2. Call Product Service to get Names and Images
        List<String> productIds = cartModel.getItems().stream().map(item -> item.getProductId().toString()).toList();

        // Call product client
        ResponseModel<List<ProductResponse>> products = productFeignClient.getProductsByIds(productIds);

        // Validate data
        if (products.getData() != null) {
            Map<String, ProductResponse> productMap = products.getData().stream().collect(Collectors.toMap(ProductResponse::getProductId, p -> p));

            cartModel.getItems().forEach(item -> {
                ProductResponse p = productMap.get(item.getProductId().toString());
                if (p != null) {
                    item.setProductName(p.getProductName());
                    item.setProductImage(p.getImagePath());
                }
            });
        }

        // 3. Map the final enriched model to the Response DTO
        log.info("Finish: Getting Cart and item by user ID: {} with successfully", userId);
        return ResponseModel.success(cartMapper.toCartResponse(cartModel));
    }

    @Override
    @Transactional
    public ResponseModel<CartResponse> toggleItemStatus(UUID userId, UUID productId, boolean isChecked) {

        log.info("START: Update item status for user: {}, product: {}, isChecked: {}",
                userId, productId, isChecked);

        //  Determine new status
        CartStatusEnum newStatus = isChecked
                ? CartStatusEnum.ACTIVE
                : CartStatusEnum.INACTIVE;

        //  Update item status
        cartDomainRepo.updateItemStatus(userId, productId, newStatus);

        //  Fetch updated cart
        CartModel cart = cartDomainRepo.getItemByUserId(userId)
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Cart not found"));

        // Recalculate total
        BigDecimal newTotal = cart.getItems().stream()
                .filter(item -> CartStatusEnum.ACTIVE.name().equals(item.getStatus()))
                .map(item -> item.getSubTotal() != null ? item.getSubTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //  Update total in DB
        cartDomainRepo.updateCartTotal(userId, newTotal);
        cart.setTotalAmount(newTotal);

        // ENRICH PRODUCT DATA (FIX HERE)
        List<String> productIds = cart.getItems().stream()
                .map(item -> item.getProductId().toString())
                .toList();

        ResponseModel<List<ProductResponse>> products =
                productFeignClient.getProductsByIds(productIds);

        if (products.getData() != null) {

            Map<String, ProductResponse> productMap =
                    products.getData().stream()
                            .collect(Collectors.toMap(
                                    ProductResponse::getProductId,
                                    p -> p
                            ));

            cart.getItems().forEach(item -> {
                ProductResponse p = productMap.get(item.getProductId().toString());
                if (p != null) {
                    item.setProductName(p.getProductName());
                    item.setProductImage(p.getImagePath());
                }
            });
        }

        log.info("FINISH: Update item status successfully. New Total: {}", newTotal);

        return ResponseModel.success(cartMapper.toCartResponse(cart));
    }

    @Override
    @Transactional
    public ResponseModel<Void> removeItemFromCart(UUID userId, UUID productId) {
        log.info("START: Removing item {} from cart for user: {}", productId, userId);

        // The repo implementation handles deletion + total recalculation
        cartDomainRepo.removeItemFromCart(userId, productId);

        log.info("FINISH: Item removed successfully");
        return ResponseModel.success();
    }

    @Override
    @Transactional
    public ResponseModel<Void> updateItemQuantity(UUID userId, UUID productId, int newQuantity) {
        log.info("START: Update quantity item {} from cart for user: {}", newQuantity, userId);

        // 1. Validate Input
        if (newQuantity <= 0) {
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        // 2. Check Stock via Inventory Service
        ResponseModel<List<InventoryResponse>> inventory = inventoryFeignClient.getBulkStock(List.of(productId.toString()));
        Integer stock = inventory.getData().stream().filter(i -> i.getProductId().equals(productId)).map(InventoryResponse::getAvailableStock).findFirst().orElse(0);

        if (stock < newQuantity) {
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Not enough stock. Available: " + stock);
        }

        // 3. Update Database
        log.info("Finish: Update quantity item {} from cart for user: {} was successfully !", newQuantity, userId);
        cartDomainRepo.updateItemQuantity(userId, productId, newQuantity);

        return ResponseModel.success();
    }

    @Override
    @Transactional
    public ResponseModel<Void> clearPurchasedItems(String userId, List<UUID> productIds) {
        log.info("Start: Starting  clear cart item for user: {}", userId);

        // validate if product exist
        if (productIds == null || productIds.isEmpty()) {
            return ResponseModel.success();
        }
        // Call clear method from repo
        cartDomainRepo.clearPurchasedItems(UUID.fromString(userId), productIds);
        log.info("Finish: Starting  clear cart item for user: {} was successfully !" , userId);
        return ResponseModel.success();
    }
}

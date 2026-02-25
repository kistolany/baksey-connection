package com.microservice.shopping_cart_service.application;

import com.microservice.shopping_cart_service.application.request.CartRequest;
import com.microservice.shopping_cart_service.application.respone.CartResponse;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import com.microservice.shopping_cart_service.domain.service.CartService;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CartResponse> createOrUpdateCart(@Valid @RequestBody CartRequest request) {
        log.info("POST: Create/Update cart with body: {}", CommonUtils.toJsonString(request));
        return cartService.createOrUpdateCart(request.getUserId().toString(), List.of(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseModel<CartResponse> getCartByUserId(@PathVariable String userId) {
        log.info("POST: Fetching cart by user id: {}", userId);
        return cartService.getCartByUserId(userId);
    }

    @PatchMapping("/items/{productId}/status")
    public ResponseModel<CartResponse> toggleItemStatus(
            @RequestParam UUID userId,
            @PathVariable UUID productId,
            @RequestParam boolean isChecked) {
        log.info("PUT:Toggling product {} to checked={} for user {}", productId, isChecked, userId);
        return cartService.toggleItemStatus(userId, productId, isChecked);
    }

    @DeleteMapping("/users/{userId}/cart/items/{productId}")
    public ResponseModel<Void> removeItem(
            @PathVariable UUID productId,
            @RequestParam("userId") String userId) {
        log.info("DELET: Request to remove product {} from cart for user {}", productId, userId);
        return cartService.removeItemFromCart(UUID.fromString(userId), productId);
    }

    @PatchMapping("/items/{productId}")
    public ResponseModel<Void> updateItemQuantity(
            @RequestParam("userId") String userId,
            @PathVariable UUID productId,
            @RequestBody CartRequest.ItemRequest items) {

        log.info("PATCH: Update quantity for user: {}, product: {}, to: {}",
                userId, productId, items.getQuantity());
        return cartService.updateItemQuantity(
                UUID.fromString(userId),
                productId,
                items.getQuantity()
        );
    }

    @DeleteMapping("/{userId}/clear-purchased")
    public ResponseModel<Void> clearPurchasedItems(
            @PathVariable String userId,
            @RequestBody List<UUID> productIds) {

        log.info("DELETE: Removing {} purchased items for user: {}", productIds.size(), userId);
        return cartService.clearPurchasedItems(userId, productIds);
    }
}


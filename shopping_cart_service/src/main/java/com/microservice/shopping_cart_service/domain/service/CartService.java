package com.microservice.shopping_cart_service.domain.service;

import com.microservice.shopping_cart_service.application.request.CartRequest;
import com.microservice.shopping_cart_service.application.respone.CartResponse;
import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface CartService {

    ResponseModel<CartResponse> createOrUpdateCart(String userId, List<CartRequest> requests);

    ResponseModel<CartResponse> getCartByUserId(String userId);

    ResponseModel<CartResponse> toggleItemStatus(UUID userId, UUID productId, boolean isChecked);

    ResponseModel<Void> removeItemFromCart(UUID userId, UUID productId);

    ResponseModel<Void> updateItemQuantity(UUID userId, UUID productId, int newQuantity);

    ResponseModel<Void> clearPurchasedItems(String userId, List<UUID> productIds);
}

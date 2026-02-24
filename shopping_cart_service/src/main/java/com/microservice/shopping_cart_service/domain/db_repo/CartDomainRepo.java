package com.microservice.shopping_cart_service.domain.db_repo;

import com.microservice.shopping_cart_service.domain.constant.Constants;
import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CartDomainRepo {

    void createOrUpdateCart(UUID userId, List<CartItemModel> items, Map<UUID, BigDecimal> priceMap);

    Optional<CartModel> getItemByUserId(UUID userId);

    void updateItemStatus(UUID userId, UUID productId, Constants.CartStatusEnum status);

    void updateCartTotal(UUID userId, BigDecimal newTotal);

    void removeItemFromCart(UUID userId, UUID productId);

    void updateItemQuantity(UUID userId, UUID productId, int quantity);

    void clearPurchasedItems(UUID userId, List<UUID> productIds);
}



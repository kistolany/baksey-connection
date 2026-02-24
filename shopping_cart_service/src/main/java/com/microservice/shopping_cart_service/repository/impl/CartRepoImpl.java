package com.microservice.shopping_cart_service.repository.impl;

import com.microservice.shopping_cart_service.domain.constant.Constants.*;
import com.microservice.shopping_cart_service.domain.db_repo.CartDomainRepo;
import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import com.microservice.shopping_cart_service.repository.CartItemRepository;
import com.microservice.shopping_cart_service.repository.CartRepository;
import com.microservice.shopping_cart_service.repository.entity.CartEntity;
import com.microservice.shopping_cart_service.repository.entity.CartItemEntity;
import com.microservice.shopping_cart_service.repository.repoMapper.CartRepoMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class CartRepoImpl implements CartDomainRepo {
    private final CartItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartRepoMapper cartRepoMapper;

    @Override
    @Transactional
    public void createOrUpdateCart(UUID userId, List<CartItemModel> items, Map<UUID, BigDecimal> priceMap) {

        String activeStatus = CartStatusEnum.ACTIVE.name();

        // 1. Create the Active Cart
        CartEntity cart = cartRepository.findByUserIdAndStatus(userId, activeStatus)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserId(userId);
                    newCart.setStatus(activeStatus);
                    newCart.setCurrency("USD");
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });

        for (CartItemModel model : items) {
            // 2. VALIDATION: Check if the product actually exists in our price list
            BigDecimal currentPrice = priceMap.get(model.getProductId());

            // call repository cart id and product id
            itemRepository.findByCartUuidAndProductId(cart.getUuid(), model.getProductId())
                    .ifPresentOrElse(existing -> {

                        // UPDATE: if request same product id
                        int newQty = existing.getQuantity() + model.getQuantity();
                        existing.setQuantity(newQty);
                        existing.setUnitPrice(currentPrice);
                        existing.setSubTotal(currentPrice.multiply(BigDecimal.valueOf(newQty)));
                        itemRepository.save(existing);
                    }, () -> {

                        // SAVE : new item row
                        CartItemEntity newItem = new CartItemEntity();
                        newItem.setCart(cart);
                        newItem.setProductId(model.getProductId());
                        newItem.setQuantity(model.getQuantity());
                        newItem.setUnitPrice(currentPrice);
                        newItem.setSubTotal(currentPrice.multiply(BigDecimal.valueOf(model.getQuantity())));
                        newItem.setStatus(activeStatus);
                        itemRepository.save(newItem);
                    });
        }
        syncCartTotal(cart);
    }

    @Override
    @Transactional
    public void updateItemStatus(UUID userId, UUID productId, CartStatusEnum status) {

        cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .flatMap(cart -> itemRepository.findByCartUuidAndProductId(cart.getUuid(), productId))
                .ifPresent(item -> {
                    item.setStatus(status.name()); // convert enum to String
                    itemRepository.save(item);
                });
    }

    @Override
    public void updateCartTotal(UUID userId, BigDecimal newTotal) {
        cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .ifPresent(cart -> {
                    cart.setTotalAmount(newTotal);
                    cartRepository.save(cart);
                });
    }

    @Override
    public Optional<CartModel> getItemByUserId(UUID userId) {

        // 1. Find the cart assigned to this user with 'ACTIVE' status
        return cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .map(cartEntity -> {

                    // 2. Fetch all items belonging to this specific cart
                    List<CartItemEntity> itemEntities = itemRepository.findAllByCartUuid(cartEntity.getUuid());

                    // 3. Map the Cart Entity to the Model
                    CartModel cartModel = cartRepoMapper.toModel(cartEntity);

                    // 4. Map Item Entities to Models and attach them to the CartModel
                    List<CartItemModel> itemModels = itemEntities.stream()
                            .map(cartRepoMapper::toItemModel)
                            .toList();

                    cartModel.setItems(itemModels);

                    return cartModel;
                });
    }

    @Override
    @Transactional
    public void removeItemFromCart(UUID userId, UUID productId) {
        cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .ifPresent(cart -> {
                    // Delete the specific item
                    itemRepository.deleteByCartUuidAndProductId(cart.getUuid(), productId);

                    // IMPORTANT: Recalculate total after deletion
                    syncCartTotal(cart);
                });
    }

    @Override
    @Transactional
    public void updateItemQuantity(UUID userId, UUID productId, int quantity) {
        cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .ifPresent(cart -> {
                    itemRepository.findByCartUuidAndProductId(cart.getUuid(), productId)
                            .ifPresent(item -> {
                                // Update Item math
                                item.setQuantity(quantity);
                                item.setSubTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
                                itemRepository.save(item);

                                // Sync the entire cart total
                                syncCartTotal(cart);
                            });
                });
    }

    @Override
    @Transactional
    public void clearPurchasedItems(UUID userId, List<UUID> productIds) {
        log.info("Repo: Clearing {} purchased items for user {}", productIds.size(), userId);

        // 1. Find the active cart
        cartRepository.findByUserIdAndStatus(userId, CartStatusEnum.ACTIVE.name())
                .ifPresent(cart -> {
                    // 2. Delete only the items provided in the list
                    itemRepository.deleteByCartUuidAndProductIdIn(cart.getUuid(), productIds);

                    // 3. Reuse your existing helper to recalculate the total amount
                    syncCartTotal(cart);
                });
    }

    // Helper method
    private void syncCartTotal(CartEntity cart) {
        // 1. Fetch items (Consider using a custom query that only selects ACTIVE items to save memory)
        List<CartItemEntity> currentItems = itemRepository.findAllByCartUuid(cart.getUuid());

        BigDecimal total = currentItems.stream()

                // 2. Filter only ACTIVE status; ignore nulls or INACTIVE
                .filter(item -> item.getStatus() != null &&
                        CartStatusEnum.ACTIVE.name().equals(item.getStatus()))

                // 3. Prefer using the pre-calculated subTotal if available
                .map(item -> item.getSubTotal() != null ? item.getSubTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Update and Save
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }


}

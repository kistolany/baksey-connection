package com.microservice.shopping_cart_service.repository.repoMapper;

import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import com.microservice.shopping_cart_service.repository.entity.CartEntity;
import com.microservice.shopping_cart_service.repository.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartRepoMapper {

    // 1. Map the parent Cart - ignore items (handled separately)
    @Mapping(target = "items", ignore = true)
    CartModel toModel(CartEntity entity);

    CartEntity toEntity(CartModel model);

    // 2. Map the Cart Items
    @Mapping(target = "cartId", source = "cart.uuid")
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productImage", ignore = true)
    CartItemModel toItemModel(CartItemEntity entity);

    // 3. Reverse mapping for saving back to DB - BaseEntity audit fields are managed by JPA
    @Mapping(target = "cart.uuid", source = "cartId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    CartItemEntity toItemEntity(CartItemModel model);
}

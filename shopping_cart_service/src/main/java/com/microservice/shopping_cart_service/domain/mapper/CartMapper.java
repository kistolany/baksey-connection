package com.microservice.shopping_cart_service.domain.mapper;

import com.microservice.shopping_cart_service.application.request.CartRequest;
import com.microservice.shopping_cart_service.application.respone.CartResponse;
import com.microservice.shopping_cart_service.domain.model.CartItemModel;
import com.microservice.shopping_cart_service.domain.model.CartModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // Map ItemRequest (from Request) to CartItemModel (Domain)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productImage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "cartId", ignore = true)
    CartItemModel toItemModel(CartRequest.ItemRequest itemRequest);

    // Map the List of items automatically
    List<CartItemModel> toItemModelList(List<CartRequest.ItemRequest> items);

    // Map CartItemModel (Domain) to the Response Item
    CartResponse.CartItemResponse toItemResponse(CartItemModel itemModel);

    // Map the full CartModel to the CartResponse
    CartResponse toCartResponse(CartModel cartModel);
}
package com.microservices.product_service.domain.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.model.ProductModel;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class, BrandMapper.class })
public interface ProductMapper {

    ProductModel toProductModel(ProductRequest productRequest);

    ProductResponse toProductResponse(ProductModel productModel);

    List<ProductResponse> toResponseList(List<ProductModel> productModels);

}

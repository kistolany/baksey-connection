package com.microservices.product_service.domain.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.model.ProductModel;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring",uses = {CategoryMapper.class, BrandMapper.class})
public interface ProductMapper {

    ProductModel toProductModel(ProductRequest productRequest);

    ProductResponse toProductResponse(ProductModel productModel);

    List<ProductResponse> toResponseList(List<ProductModel> productModels);

}

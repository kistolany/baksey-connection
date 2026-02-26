package com.microservices.product_service.domain.mapper;

import com.microservices.product_service.application.response.ProductImportResponse;
import com.microservices.product_service.domain.model.ProductImportModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImportMapper {

    ProductImportResponse toResponse(ProductImportModel model);

    List<ProductImportResponse> toResponseList(List<ProductImportModel> models);
}

package com.microservices.payment_service.domain.mapper;

import com.microservices.product_service.application.request.BrandRequest;
import com.microservices.product_service.application.response.BrandResponse;
import com.microservices.product_service.domain.model.BrandModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandModel toBrandModel(BrandRequest brandRequest);
    BrandResponse toBrandResponse(BrandModel brandModel);
}

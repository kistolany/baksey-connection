package com.microservices.payment_service.infrastructure.repository.repoMapper;

import com.microservices.product_service.domain.model.BrandModel;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandRepoMapper {
	
	BrandEntity toBrandEntity(BrandModel brandModel);
    BrandModel toBrandModel(BrandEntity brandEntity);

}

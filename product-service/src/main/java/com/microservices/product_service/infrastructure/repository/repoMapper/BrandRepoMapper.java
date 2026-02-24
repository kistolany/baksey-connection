package com.microservices.product_service.infrastructure.repository.repoMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.microservices.product_service.domain.model.BrandModel;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;

@Mapper(componentModel = "spring")
public interface BrandRepoMapper {
	
	BrandEntity toBrandEntity(BrandModel brandModel);

    BrandModel toBrandModel(BrandEntity brandEntity);

}

package com.microservices.product_service.infrastructure.repository.repoMapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.product_service.domain.model.CategoryModel;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryRepoMapper {
	
	@Mapping(target = "brand", source = "brandId")
    CategoryEntity toCategoryEntity(CategoryModel categoryModel);

    @Mapping(target = "brandId", source = "brand.uuid")
    CategoryModel toCategoryModel(CategoryEntity categoryEntity);
    
    
    default BrandEntity mapIdToBrand(UUID id) {
        if (id == null) return null;
        BrandEntity brand = new BrandEntity();
        brand.setUuid(id);
        return brand;
    }

}

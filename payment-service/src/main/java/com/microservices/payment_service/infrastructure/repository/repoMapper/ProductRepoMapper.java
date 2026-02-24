package com.microservices.payment_service.infrastructure.repository.repoMapper;

import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;
import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductRepoMapper {

    @Mapping(target = "brand", source = "brandId")
    @Mapping(target = "category", source = "categoryId")
    ProductEntity toProductEntity(ProductModel productModel);

    @Mapping(target = "brandId", source = "brand.uuid")
    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "categoryId", source = "category.uuid")
    ProductModel toProductModel(ProductEntity productEntity);

    default CategoryEntity mapUuidToCategory(UUID categoryId) {
        if (categoryId == null) return null;
        CategoryEntity category = new CategoryEntity();
        category.setUuid(categoryId);
        return category;
    }

    default BrandEntity uuidToBrand(UUID uuid) {
        if (uuid == null) return null;
        BrandEntity b = new BrandEntity();
        b.setUuid(uuid);
        return b;
    }

}

package com.microservices.product_service.infrastructure.repository.repoMapper;

import com.microservices.product_service.domain.model.ProductImportModel;
import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;
import com.microservices.product_service.infrastructure.repository.entity.ProductImportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductImportRepoMapper {

    @Mapping(target = "product", source = "productId")
    ProductImportEntity toEntity(ProductImportModel model);

    @Mapping(target = "productId", source = "product.uuid")
    @Mapping(target = "id", source = "uuid")
    ProductImportModel toModel(ProductImportEntity entity);

    default ProductEntity mapProductIdToProductEntity(UUID productId) {
        if (productId == null) {
            return null;
        }
        ProductEntity productEntity = new ProductEntity();
        productEntity.setUuid(productId);
        return productEntity;
    }
}

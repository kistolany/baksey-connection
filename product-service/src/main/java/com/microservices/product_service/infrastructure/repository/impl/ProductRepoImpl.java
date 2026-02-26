package com.microservices.product_service.infrastructure.repository.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.domain.db_repo.ProductDomainRepo;
import com.microservices.product_service.domain.filter.ProductFilterSpecification;
import com.microservices.product_service.domain.constant.Constants;
import com.microservices.product_service.infrastructure.repository.ProductRepository;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;
import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;
import com.microservices.product_service.infrastructure.repository.repoMapper.ProductRepoMapper;

import lombok.AllArgsConstructor;

@Slf4j
@AllArgsConstructor
@Component
public class ProductRepoImpl implements ProductDomainRepo {
    private final ProductRepoMapper productRepoMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductModel create(String productName, String description, BigDecimal salePrice, String categoryId,
            String brandId, Constants.ProductStatus status) {

        // Build the Domain Model
        ProductModel productModel = ProductModel.builder()
                .name(productName)
                .description(description)
                .salePrice(salePrice)
                .categoryId(UUID.fromString(categoryId))
                .brandId(UUID.fromString(brandId))
                .status(status != null ? status : Constants.ProductStatus.DRAFT)
                .build();

        // Map to Entity
        ProductEntity productEntity = productRepoMapper.toProductEntity(productModel);

        // Save to Database
        ProductEntity saved = productRepository.save(productEntity);

        // Return mapped Model
        return productRepoMapper.toProductModel(saved);
    }

    @Override
    public Optional<ProductModel> getById(String id) {

        // find one of id and return
        return productRepository.findById(UUID.fromString(id))

                // map entity to model
                .map(productRepoMapper::toProductModel);
    }

    @Override
    public ProductModel update(ProductModel record, String productName, String description, BigDecimal salePrice,
            String categoryId, String brandId, Constants.ProductStatus status, String id) {

        // Fetch the existing entity to preserve status
        ProductEntity productEntity = productRepository.findById(UUID.fromString(id)).get();

        // Update only the necessary fields
        productEntity.setName(productName);
        productEntity.setDescription(description);
        productEntity.setSalePrice(salePrice);

        // Update status if provided, otherwise keep existing
        if (status != null) {
            productEntity.setStatus(status);
        }

        // Update brand and category entities
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setUuid(UUID.fromString(categoryId));
        productEntity.setCategory(categoryEntity);

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setUuid(UUID.fromString(brandId));
        productEntity.setBrand(brandEntity);

        // Save and return mapped model
        productEntity = productRepository.save(productEntity);
        return productRepoMapper.toProductModel(productEntity);
    }

    @Override
    public Page<ProductModel> getAllByCategoryUuid(String categoryId, Pageable pageable) {

        // call find all by category product by category id
        Page<ProductEntity> listByCategoryUuid = productRepository.findAllByCategoryUuid(UUID.fromString(categoryId),
                pageable);

        // map entity to model and return
        return listByCategoryUuid.map(productRepoMapper::toProductModel);
    }

    @Override
    public Page<ProductModel> searchProducts(ProductFilterSpecification spec, Pageable pageable) {

        // Execute the search using JPA Repository
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map Entity to Model Return the wrapped PageResponse
        return productPage.map(productRepoMapper::toProductModel);
    }

    @Override
    public List<ProductModel> findProductIds(List<UUID> productIds) {
        log.info("Repo: Fetching product entities for {} IDs", productIds.size());

        // 1. Fetch product using the IN clause
        List<ProductEntity> entities = productRepository.findByUuidIn(productIds);

        // 2. Map to Domain Model and return
        return entities.stream().map(productRepoMapper::toProductModel).toList();
    }

    @Override
    @Transactional
    public void updateProductImage(UUID productId, List<String> fileName) {

        // find product id
        ProductEntity productEntity = productRepository.findById(productId).get();

        // set image path
        productEntity.setImages(fileName);

        // save to entity
        productRepository.save(productEntity);
    }

    @Override
    @Transactional
    public ProductModel updateStatus(String id, Constants.ProductStatus status) {

        // find product by id
        ProductEntity productEntity = productRepository.findById(UUID.fromString(id)).get();

        // update status
        productEntity.setStatus(status);

        // save and return mapped model
        return productRepoMapper.toProductModel(productRepository.save(productEntity));
    }

}

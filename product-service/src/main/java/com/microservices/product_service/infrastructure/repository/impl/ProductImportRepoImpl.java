package com.microservices.product_service.infrastructure.repository.impl;

import com.microservices.product_service.domain.db_repo.ProductImportDomainRepo;
import com.microservices.product_service.domain.model.ProductImportModel;
import com.microservices.product_service.infrastructure.repository.ProductImportRepository;
import com.microservices.product_service.infrastructure.repository.entity.ProductImportEntity;
import com.microservices.product_service.infrastructure.repository.repoMapper.ProductImportRepoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class ProductImportRepoImpl implements ProductImportDomainRepo {
    private final ProductImportRepository productImportRepository;
    private final ProductImportRepoMapper productImportRepoMapper;

    @Override
    public ProductImportModel create(UUID productId, Integer quantity, BigDecimal pricePerUnit, String currency,
            String description) {
        log.info("Saving product import for product id: {}", productId);
        ProductImportModel model = ProductImportModel.builder()
                .productId(productId)
                .quantity(quantity)
                .pricePerUnit(pricePerUnit)
                .currency(currency)
                .description(description)
                .build();
        ProductImportEntity entity = productImportRepoMapper.toEntity(model);
        ProductImportEntity savedEntity = productImportRepository.save(entity);
        return productImportRepoMapper.toModel(savedEntity);
    }

    @Override
    public org.springframework.data.domain.Page<ProductImportModel> getAll(
            org.springframework.data.domain.Pageable pageable) {
        return productImportRepository.findAll(pageable)
                .map(productImportRepoMapper::toModel);
    }

    @Override
    public java.util.Optional<ProductImportModel> getById(String id) {
        return productImportRepository.findById(UUID.fromString(id))
                .map(productImportRepoMapper::toModel);
    }

    @Override
    public org.springframework.data.domain.Page<ProductImportModel> getByProductId(String productId,
            org.springframework.data.domain.Pageable pageable) {
        return productImportRepository.findByProductUuid(UUID.fromString(productId), pageable)
                .map(productImportRepoMapper::toModel);
    }
}

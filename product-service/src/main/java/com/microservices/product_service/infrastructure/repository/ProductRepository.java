package com.microservices.product_service.infrastructure.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsByNameAndCategoryUuidAndBrandUuid(String productName, UUID categoryUuid, UUID brandUuid);

    Page<ProductEntity> findAllByCategoryUuid(UUID uuid, Pageable pageable);

    List<ProductEntity> findByUuidIn(List<UUID> productIds);

}

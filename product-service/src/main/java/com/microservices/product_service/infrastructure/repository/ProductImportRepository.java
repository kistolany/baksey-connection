package com.microservices.product_service.infrastructure.repository;

import com.microservices.product_service.infrastructure.repository.entity.ProductImportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface ProductImportRepository extends JpaRepository<ProductImportEntity, UUID> {

    @Query("SELECT COALESCE(SUM(pi.quantity), 0) FROM ProductImportEntity pi WHERE pi.product.id = :productId")
    Integer sumQuantityByProductId(@Param("productId") UUID productId);

    Page<ProductImportEntity> findByProductUuid(UUID productId, Pageable pageable);
}

package com.microservices.payment_service.infrastructure.repository;

import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {
    boolean existsByBrandName(String brandName);
}

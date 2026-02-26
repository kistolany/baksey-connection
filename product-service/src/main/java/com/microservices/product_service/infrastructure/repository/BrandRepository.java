package com.microservices.product_service.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;

import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {
    boolean existsByName(String brandName);
}

package com.microservices.product_service.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    boolean existsByNameAndBrandUuid(String name, UUID brandUuid);

    List<CategoryEntity> findAllByBrandUuid(UUID brandUuid);
}

package com.microservices.product_service.domain.db_repo;

import com.microservices.product_service.domain.model.BrandModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandDomainRepo {
    BrandModel create(String brandName);

    Optional<BrandModel> getById(String id);

    List<BrandModel> getAll();

    BrandModel update(BrandModel oldRecord, String brandName,String imagePath);

    void updateBrandImage(UUID brandId, String imagePath);
}

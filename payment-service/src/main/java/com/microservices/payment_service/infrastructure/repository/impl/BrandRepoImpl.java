package com.microservices.product_service.infrastructure.repository.impl;

import com.microservices.product_service.domain.model.BrandModel;
import com.microservices.product_service.domain.db_repo.BrandDomainRepo;
import com.microservices.product_service.infrastructure.repository.BrandRepository;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;
import com.microservices.product_service.infrastructure.repository.repoMapper.BrandRepoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class BrandRepoImpl implements BrandDomainRepo {
    private final BrandRepository brandRepository;
    private final BrandRepoMapper brandRepoMapper;

    @Override
    public BrandModel create(String brandName) {

        // using builder for set data
        BrandModel brandModel = BrandModel.builder().brandName(brandName).build();
        BrandEntity brand = brandRepoMapper.toBrandEntity(brandModel);
        BrandEntity brandEntity = brandRepository.save(brand);
        return brandRepoMapper.toBrandModel(brandEntity);
    }

    @Override
    public Optional<BrandModel> getById(String id) {
        return brandRepository.findById(UUID.fromString(id))
                .map(brandRepoMapper::toBrandModel);
    }

    @Override
    public List<BrandModel> getAll() {
        List<BrandEntity> BrandEntity = brandRepository.findAll();

        // map entity to model and return
        return BrandEntity.stream().map(brandRepoMapper::toBrandModel).toList();
    }

    @Override
    public BrandModel update(BrandModel oldRecord, String brandName,String imagePath) {

        oldRecord.setBrandName(brandName);
        oldRecord.setImageUrl(imagePath);

        // map to entity
        BrandEntity brandEntity = brandRepoMapper.toBrandEntity(oldRecord);

        // save entity
        brandEntity = brandRepository.save(brandEntity);

        // map to model and return it
        return brandRepoMapper.toBrandModel(brandEntity);
    }

    @Override
    public void updateBrandImage(UUID brandId, String imagePath) {
        // find product id
        BrandEntity brand = brandRepository.findById(brandId).get();

        // set image path
        brand.setImageUrl(imagePath);

        // save to entity
        brandRepository.save(brand);
    }

}

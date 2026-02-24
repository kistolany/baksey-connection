package com.microservices.product_service.infrastructure.repository.impl;

import com.microservices.common_service.domain.PageResponse;
import com.microservices.product_service.domain.model.CategoryModel;
import com.microservices.product_service.domain.db_repo.CategoryDomainRepo;
import com.microservices.product_service.infrastructure.repository.CategoryRepository;
import com.microservices.product_service.infrastructure.repository.entity.BrandEntity;
import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;
import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;
import com.microservices.product_service.infrastructure.repository.repoMapper.CategoryRepoMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class CategoryRepoImpl implements CategoryDomainRepo {
    private final CategoryRepository categoryRepository;
    private final CategoryRepoMapper categoryRepoMapper;

    @Override
    public CategoryModel create(String categoryName, String brandId) {
        CategoryModel categoryModel = CategoryModel.builder()
                .name(categoryName)
                .brandId(UUID.fromString(brandId))
                .build();

        // map model to entity
        CategoryEntity categoryEntity = categoryRepoMapper.toCategoryEntity(categoryModel);

        // save entity
        categoryRepository.save(categoryEntity);

        // map to model and return
        return categoryRepoMapper.toCategoryModel(categoryEntity);
    }

    @Override
    public Optional<CategoryModel> getById(String id) {
        return categoryRepository.findById(UUID.fromString(id))
                // map entity to model
                .map(categoryRepoMapper::toCategoryModel);
    }

    @Override
    public Page<CategoryModel> getAll(Pageable pageable) {
        // 1. Fetch paged entities from DB
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(pageable);

        // 2. Map Page<Entity> to Page<Model> & return it
        return categoryPage.map(categoryRepoMapper::toCategoryModel);
    }

    @Override
    public CategoryModel update(String categoryName, String brandId, String id) {

        // Fetch the existing entity to preserve status and other fields
        CategoryEntity categoryEntity = categoryRepository.findById(UUID.fromString(id)).get();

        // Update only the necessary fields
        categoryEntity.setName(categoryName);
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setUuid(UUID.fromString(brandId));
        categoryEntity.setBrand(brandEntity);

        // save entity
        CategoryEntity EntitySave = categoryRepository.save(categoryEntity);

        // map entity to model and return it
        return categoryRepoMapper.toCategoryModel(EntitySave);
    }

    @Override
    public List<CategoryModel> getAllByBrandUuid(String BrandUuid) {

        // find all category by brand id
        List<CategoryEntity> allByBrandUuid = categoryRepository.findAllByBrandUuid(UUID.fromString(BrandUuid));

        // map to model and return it
        return allByBrandUuid.stream().map(categoryRepoMapper::toCategoryModel).toList();
    }


    @Override
    @Transactional
    public void updateCategoryImage(UUID productId, String fileName) {

        // find product id
        CategoryEntity categoryEntity = categoryRepository.findById(productId).get();

        // set image path
        categoryEntity.setImageUrl(fileName);

        // save to entity
        categoryRepository.save(categoryEntity);
    }
}

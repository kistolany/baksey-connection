package com.microservices.product_service.domain.db_repo;

import com.microservices.common_service.domain.PageResponse;
import com.microservices.product_service.domain.model.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDomainRepo {

    CategoryModel create(String brandName, String branId);

    Optional<CategoryModel> getById(String id);

    Page<CategoryModel> getAll(Pageable pageable);

    CategoryModel update(String categoryName, String branId, String id);

    List<CategoryModel> getAllByBrandUuid(String BrandUuid);

    void updateCategoryImage(UUID categoryId, String fileName);
}

package com.microservices.product_service.domain.db_repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.microservices.product_service.domain.model.ProductModel;
import com.microservices.product_service.domain.filter.ProductFilterSpecification;
import com.microservices.product_service.domain.constant.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductDomainRepo {

    ProductModel create(String productName, String description, BigDecimal salePrice, String categoryId, String brandId,
            Constants.ProductStatus status);

    Optional<ProductModel> getById(String id);

    ProductModel update(ProductModel record, String productName, String description, BigDecimal salePrice,
            String categoryId, String brandId, Constants.ProductStatus status, String id);

    Page<ProductModel> getAllByCategoryUuid(String categoryId, Pageable pageable);

    Page<ProductModel> searchProducts(ProductFilterSpecification spec, Pageable pageable);

    List<ProductModel> findProductIds(List<UUID> productIds);

    void updateProductImage(UUID productId, List<String> file);

    ProductModel updateStatus(String id, Constants.ProductStatus status);
}

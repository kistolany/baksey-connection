package com.microservices.payment_service.domain.db_repo;

import com.microservices.product_service.application.response.ProductSpecification;
import com.microservices.product_service.domain.model.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDomainRepo {

    ProductModel create(String productName, String description, BigDecimal salePrice, String categoryId, String brandId);

    Optional<ProductModel> getById(String id);

    ProductModel update(ProductModel record,String productName, String description, BigDecimal salePrice, String categoryId,String brandId, String id);

    Page<ProductModel> getAllByCategoryUuid(String categoryId, Pageable pageable);

    Page<ProductModel> searchProducts(ProductSpecification spec, Pageable pageable);

    List<ProductModel> findProductIds(List<UUID> productIds);

    void updateProductImage(UUID productId, String file);

}

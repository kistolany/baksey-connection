package com.microservices.product_service.domain.db_repo;

import com.microservices.product_service.domain.model.ProductImportModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductImportDomainRepo {
    ProductImportModel create(UUID productId, Integer quantity, BigDecimal pricePerUnit, String currency,
            String description);

    Page<ProductImportModel> getAll(Pageable pageable);

    Optional<ProductImportModel> getById(String id);

    Page<ProductImportModel> getByProductId(String productId, Pageable pageable);
}

package com.microservices.product_service.application.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.microservices.product_service.application.request.ProductSearchRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import com.microservices.product_service.infrastructure.repository.entity.ProductEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public record ProductSpecification(ProductSearchRequest productFilter) implements Specification<ProductEntity> {
    @Override
    public @Nullable Predicate toPredicate(@NonNull Root<ProductEntity> product, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {

        // declare writable for list predicate
        List<Predicate> predicates = new ArrayList<Predicate>();

        // filter by product name
        if (Objects.nonNull(productFilter.getName())) {
            Predicate predicate = cb.like(cb.lower(product.get("name")), "%" + productFilter.getName().toLowerCase() + "%");
            predicates.add(predicate);
        }

        // filter id product
        if (Objects.nonNull(productFilter.getId())) {
            predicates.add(cb.equal(product.get("uuid"), UUID.fromString(productFilter.getId())));
        }

        // Filter by Category Name (String)
        if (productFilter.getCategory() != null && !productFilter.getCategory().isBlank()) {

            // We JOIN to the category table and use LOWER() on the categoryName column
            predicates.add(cb.equal(cb.lower(product.join("category").get("name")), productFilter.getCategory().toLowerCase()));
        }
        // Filter by Brand Name (String)
        if (productFilter.getBrand() != null && !productFilter.getBrand().isBlank()) {

            // We JOIN to the brand table and use LOWER() on the brandName column
            predicates.add(cb.equal(cb.lower(product.join("brand").get("name")), productFilter.getBrand().toLowerCase()));
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

}

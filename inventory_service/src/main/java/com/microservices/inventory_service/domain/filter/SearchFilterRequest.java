package com.microservices.inventory_service.domain.filter;

import com.microservices.inventory_service.infrastructure.repository.entity.InventoryEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class SearchFilterRequest implements Specification<InventoryEntity> {
    private final FilterDTO filterDTO;
    private final List<UUID> allowedProductIds;

    @Override
    public Predicate toPredicate(Root<InventoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        // 1. Stock Filtering (Database Level)
        if (filterDTO.getMinStock() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("availableStock"), filterDTO.getMinStock()));
        }
        if (filterDTO.getMaxStock() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("availableStock"), filterDTO.getMaxStock()));
        }

        // 2. Product Filtering (Injected from Product Service search)
        if (allowedProductIds != null) {
            predicates.add(root.get("productId").in(allowedProductIds));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
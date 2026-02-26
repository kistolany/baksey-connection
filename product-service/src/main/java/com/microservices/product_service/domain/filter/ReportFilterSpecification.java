package com.microservices.product_service.domain.filter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import com.microservices.product_service.infrastructure.repository.entity.ProductImportEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReportFilterSpecification implements Specification<ProductImportEntity> {

    private final ReportFilterRequest reportFilter;

    @Override
    public @Nullable Predicate toPredicate(@NonNull Root<ProductImportEntity> root, @NonNull CriteriaQuery<?> query,
            @NonNull CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        // Filter by start date
        if (reportFilter.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), reportFilter.getStartDate().atStartOfDay()));
        }

        // Filter by end date
        if (reportFilter.getEndDate() != null) {
            predicates
                    .add(cb.lessThanOrEqualTo(root.get("createdAt"), reportFilter.getEndDate().atTime(LocalTime.MAX)));
        }

        return predicates.isEmpty() ? null : cb.and(predicates.toArray(Predicate[]::new));
    }

}

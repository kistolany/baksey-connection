package com.microservices.payment_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "category")
public class CategoryEntity extends BaseEntity {
    private String categoryName;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "brandId")
    private BrandEntity brand;
}

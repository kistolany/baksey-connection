package com.microservices.product_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
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

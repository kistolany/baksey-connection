package com.microservices.product_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "product")
@Entity
public class ProductEntity extends BaseEntity {
	private String productName;
	private String description;
    private BigDecimal salePrice;
    private  String imagePath;
    @ManyToOne
	@JoinColumn(name = "categoryId")
    private CategoryEntity category;
	@ManyToOne
	@JoinColumn(name = "brandId")
	private BrandEntity brand;

}

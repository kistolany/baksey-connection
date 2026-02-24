package com.microservices.payment_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

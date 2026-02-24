package com.microservices.product_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "product")
@Entity
public class ProductEntity extends BaseEntity {
	private String name;
	private String description;
    private BigDecimal salePrice;
    @ManyToOne
	@JoinColumn(name = "categoryId")
    private CategoryEntity category;
	@ManyToOne
	@JoinColumn(name = "brandId")
	private BrandEntity brand;

	@ElementCollection
	@CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
	@Column(name = "image_url")
	private List<String> images = new ArrayList<>();

}

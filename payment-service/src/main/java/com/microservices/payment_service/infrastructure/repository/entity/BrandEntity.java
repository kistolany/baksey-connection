package com.microservices.product_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "brand")
public class BrandEntity extends BaseEntity {
	private String brandName;
	private String imageUrl;
}

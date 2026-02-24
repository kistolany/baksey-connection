package com.microservices.payment_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

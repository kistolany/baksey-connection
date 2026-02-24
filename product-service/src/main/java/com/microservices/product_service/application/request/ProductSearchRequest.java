package com.microservices.product_service.application.request;

import com.microservices.product_service.infrastructure.repository.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSearchRequest {
	private String id;
	private String name;
	private String category;
	private String brand;
}


package com.microservices.product_service.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductFilterRequest {
	private String id;
	private String name;
	private String category;
	private String brand;
	private String minPrice;
	private String maxPrice;
}

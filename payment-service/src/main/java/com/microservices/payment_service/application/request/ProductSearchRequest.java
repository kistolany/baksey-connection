package com.microservices.payment_service.application.request;

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


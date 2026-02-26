package com.microservices.product_service.application.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
	@NotBlank(message = "categoryName cannot be blank")
	private String name;
	private UUID brandId;
	private String imageUrl;
}

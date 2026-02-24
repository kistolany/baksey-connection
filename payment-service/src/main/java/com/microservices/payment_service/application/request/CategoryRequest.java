package com.microservices.payment_service.application.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryRequest {
	@NotBlank(message = "categoryName cannot be blank")
    private String categoryName;
	private UUID brandId;
}

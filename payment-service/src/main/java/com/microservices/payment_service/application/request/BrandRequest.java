package com.microservices.payment_service.application.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
	@NotBlank(message = "brandName cannot be blank")
    private String brandName;
    private String imageUrl;
}

package com.microservices.product_service.application.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "brandName cannot be blank")
    private String name;

    private String imageUrl;
}

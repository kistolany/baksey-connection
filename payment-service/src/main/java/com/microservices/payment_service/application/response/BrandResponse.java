package com.microservices.product_service.application.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BrandResponse {
    private UUID uuid;
    private String brandName;
    private String imageUrl;
}

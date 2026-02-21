package com.microservices.product_service.application.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID uuid;
    private String categoryName;
    private String imageUrl;
    private UUID brandId;
}

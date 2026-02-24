package com.microservices.payment_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryModel {
    private UUID uuid;
    private String categoryName;
    private String imageUrl;
    private UUID brandId;
}

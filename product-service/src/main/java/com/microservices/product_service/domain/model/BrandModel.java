package com.microservices.product_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BrandModel {
    private UUID uuid;
    private String name;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

package com.microservices.payment_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BrandModel {
    private UUID uuid;
    private String brandName;
    private String imageUrl;
}

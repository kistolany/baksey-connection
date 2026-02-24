package com.microservices.product_service.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BrandResponse {
    private UUID uuid;
    private String name;
    private String imageUrl;
    @JsonProperty("creationAt")
    private LocalDateTime createdAt;
    @JsonProperty("updatedAt")
    private LocalDateTime lastUpdatedAt;
}

package com.microservices.product_service.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID uuid;
    private String name;
    private String imageUrl;
    private UUID brandId;
    @JsonProperty("creationAt")
    private LocalDateTime createdAt;
    @JsonProperty("updatedAt")
    private LocalDateTime lastUpdatedAt;
}

package com.microservices.inventory_service.domain.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    @JsonProperty("uuid")
    private UUID uuid;
    private String name;
    private String imageUrl;
}
package com.microservice.shopping_cart_service.domain.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequest {
    @JsonProperty("uuid")
    private UUID orderId;

    @JsonProperty("customerId")
    private UUID customerId;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private String status;

}

package com.microservices.order_service.application.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesByProvinceResponse {
    private String province;
    private Long totalOrders;
    private BigDecimal totalSales;
}


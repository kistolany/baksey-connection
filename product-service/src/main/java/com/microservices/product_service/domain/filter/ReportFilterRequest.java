package com.microservices.product_service.domain.filter;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterRequest {

    private LocalDate startDate;
    private LocalDate endDate;

}

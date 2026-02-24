package com.microservices.product_service.application;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/reports")
public class ReportController {

    /*
    @GetMapping(value = "/product-import/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseModel<List<ImportReport>>> importReportPath(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<ImportReport> response = reportService.productImportReport(startDate, endDate);
        return ResponseEntity.ok(ResponseModel.success(response));
    }
     */


}

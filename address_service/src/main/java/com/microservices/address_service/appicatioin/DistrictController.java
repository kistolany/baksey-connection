package com.microservices.address_service.appicatioin;

import com.microservices.address_service.appicatioin.request.DistrictRequest;
import com.microservices.address_service.appicatioin.response.DistrictResponse;
import com.microservices.address_service.domain.service.DistrictService;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/districts")
public class DistrictController {

    private final DistrictService districtService;

    @PostMapping
    public ResponseModel<DistrictResponse> create(@Valid @RequestBody DistrictRequest request) {
        log.info("POST: Create district with body: {}", request);
        return districtService.save(request);
    }

    @GetMapping
    public ResponseModel<List<DistrictResponse>> getAll() {
        log.info("GET: List all districts");
        return districtService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseModel<DistrictResponse> getById(@PathVariable UUID id) {
        log.info("GET: District by id: {}", id);
        return districtService.findById(id);
    }

    @GetMapping("/province/{provinceId}")
    public ResponseModel<List<DistrictResponse>> getByProvinceId(@PathVariable UUID provinceId) {
        log.info("GET: Districts by province id: {}", provinceId);
        return districtService.findByProvinceId(provinceId);
    }

    @PutMapping("/{id}")
    public ResponseModel<DistrictResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DistrictRequest request
    ) {
        log.info("PUT: Districts with body: {}", CommonUtils.toJsonString(request));
        return districtService.update(id, request);
    }
}
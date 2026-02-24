package com.microservices.address_service.appicatioin;

import com.microservices.address_service.appicatioin.request.ProvinceRequest;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.domain.service.ProvinceService;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/provinces")
public class ProvinceController {

    private final ProvinceService provinceService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProvinceResponse> create(@Valid @RequestBody ProvinceRequest request) {
        log.info("POST: Create province with body: {}", CommonUtils.toJsonString(request));
        return provinceService.save(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<ProvinceResponse>> findAll() {
        log.info("GET: All provinces");
        return provinceService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProvinceResponse> findById(@PathVariable UUID id) {
        log.info("GET: Province by id: {}", id);
        return provinceService.findById(id);
    }

    @GetMapping(value = "/code/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<ProvinceResponse> findByCode(@PathVariable String code) {
        log.info("GET: Province by code: {}", code);
        return provinceService.findByCode(code);
    }
}
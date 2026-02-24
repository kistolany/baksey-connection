package com.microservices.address_service.appicatioin;

import com.microservices.address_service.appicatioin.request.CommuneRequest;
import com.microservices.address_service.appicatioin.response.CommuneResponse;
import com.microservices.address_service.domain.service.CommuneService;
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
@RequestMapping(value = "/api/v1/communes")
public class CommuneController {

    private final CommuneService communeService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CommuneResponse> create(@Valid @RequestBody CommuneRequest request) {
        log.info("POST: Create commune with body: {}", CommonUtils.toJsonString(request));
        return communeService.save(request);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CommuneResponse> update(@PathVariable UUID id, @Valid @RequestBody CommuneRequest request) {
        log.info("PUT: Update commune ID: {} with body: {}", id, CommonUtils.toJsonString(request));
        return communeService.update(id, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<CommuneResponse>> findAll() {
        log.info("GET: All communes");
        return communeService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CommuneResponse> findById(@PathVariable UUID id) {
        log.info("GET: Commune by id: {}", id);
        return communeService.findById(id);
    }

    @GetMapping(value = "/district/{districtId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<CommuneResponse>> findByDistrictId(@PathVariable UUID districtId) {
        log.info("GET: Communes by district id: {}", districtId);
        return communeService.findByDistrictId(districtId);
    }
}
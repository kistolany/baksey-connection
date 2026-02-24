package com.microservices.product_service.application;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.BrandRequest;
import com.microservices.product_service.application.response.BrandResponse;
import com.microservices.product_service.domain.service.BrandService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/brands")
public class BrandController {
    private final BrandService brandService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        log.info("POST: Create brand with request body : {}", CommonUtils.toJsonString(request));
        return brandService.create(request);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<BrandResponse> getOneBrand(@Valid @PathVariable UUID id) {
        log.info("GET: Getting brand with id : {}", id);
        return brandService.getById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<BrandResponse>> getAllBrand() {
        log.info("GET: Getting all brand !");
        return brandService.getAll();
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<BrandResponse> updateBrand(@Valid @RequestBody BrandRequest brandRequest, @PathVariable String id) {
        log.info("PUT: Update brand with id {} ", id);
        return brandService.update(brandRequest, id);
    }

    @PostMapping(
            value = "/{brandId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel<String> uploadBrandImage(
            @PathVariable UUID brandId,
            @RequestParam("file") MultipartFile file) {
        log.info("PUT: Upload image to brand with id {} ", brandId);
        return brandService.uploadImage(brandId, file);
    }

}

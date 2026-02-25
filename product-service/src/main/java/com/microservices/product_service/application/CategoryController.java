package com.microservices.product_service.application;

import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.CategoryRequest;
import com.microservices.product_service.application.response.CategoryResponse;
import com.microservices.product_service.domain.service.CategoryService;
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
@RequestMapping(value = "/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("POST: Create category with request body : {}", CommonUtils.toJsonString(request));
        return categoryService.create(request);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CategoryResponse> getOneCategory(@Valid @PathVariable String id) {
        log.info("GET: Getting category with id : {}", id);
        return categoryService.getById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<PageResponse<CategoryResponse>> getAll(@Valid @ModelAttribute PageRequest pageRequest) {
        log.info("GET: Getting all categories with params: {}", pageRequest);
        return categoryService.getAll(pageRequest);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<CategoryResponse> updateCategory(@Valid @RequestBody CategoryRequest brandRequest, @PathVariable String id) {
        log.info("PUT: Update category with id {} ", id);
        return categoryService.update(brandRequest, id);
    }

    @GetMapping(value = "/brand/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<CategoryResponse>> getAllCategoryById(@Valid @PathVariable String id) {
        log.info("Get: Getting category by brand id {} ", id);
        return categoryService.getAllByBrandUuid(id);
    }

    @PostMapping(
            value = "/{categoryId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel<String> upload(
            @PathVariable UUID categoryId,
            @RequestParam("file") MultipartFile file) {
        log.info("POST: Upload image category by id {} ", categoryId);
        return categoryService.uploadImage(categoryId, file);
    }

}

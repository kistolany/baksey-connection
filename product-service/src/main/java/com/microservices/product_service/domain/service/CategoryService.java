package com.microservices.product_service.domain.service;

import java.util.List;
import java.util.UUID;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.CategoryRequest;
import com.microservices.product_service.application.response.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {

    ResponseModel<CategoryResponse> create(CategoryRequest request);

    ResponseModel<CategoryResponse> getById(String id);

    ResponseModel<PageResponse<CategoryResponse>> getAll(PageRequest pageRequest);

    ResponseModel<CategoryResponse> update(CategoryRequest categoryRequest, String id);

    ResponseModel<List<CategoryResponse>> getAllByBrandUuid(String BrandUuid);

    ResponseModel<List<String>> uploadImage(UUID categoryId, List<MultipartFile> file);

}

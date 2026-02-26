package com.microservices.product_service.domain.service;

import java.util.List;
import java.util.UUID;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.application.request.BrandRequest;
import com.microservices.product_service.application.response.BrandResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BrandService {

    ResponseModel<BrandResponse> create(BrandRequest request);

    ResponseModel<BrandResponse> getById(UUID id);

    ResponseModel<List<BrandResponse>> getAll();

    ResponseModel<BrandResponse> update(BrandRequest brandRequest, String id);

    ResponseModel<List<String>> uploadImage(UUID brandId, List<MultipartFile> files);
}
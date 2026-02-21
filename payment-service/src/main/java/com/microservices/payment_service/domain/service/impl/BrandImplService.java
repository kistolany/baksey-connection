package com.microservices.product_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.BrandRequest;
import com.microservices.product_service.application.response.BrandResponse;
import com.microservices.product_service.domain.mapper.BrandMapper;
import com.microservices.product_service.domain.model.BrandModel;
import com.microservices.product_service.domain.db_repo.BrandDomainRepo;
import com.microservices.product_service.domain.outbound.feignclient.AttachmentClient;
import com.microservices.product_service.domain.service.BrandService;
import com.microservices.product_service.infrastructure.repository.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@Service
public class BrandImplService implements BrandService {
    private final BrandDomainRepo brandDomainRepo;
    private final BrandMapper brandMapper;
    private final BrandRepository brandRepository;
    private final AttachmentClient attachmentClient;

    @Override
    public ResponseModel<BrandResponse> create(BrandRequest request) {
        log.info("Start: Creating brand by request body {}", CommonUtils.toJsonString(request));

        // call method for validate exist name
        boolean isDuplicate = brandRepository.existsByBrandName(request.getBrandName());
        if (isDuplicate) {
            log.error("Brand name {} is already exist!",request.getBrandName());
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, String.format("Brand name %s is already exist!", request.getBrandName()));
        }

        // call create method from repo for save
        BrandModel brandModel = brandDomainRepo.create(request.getBrandName());
        BrandResponse brandResponse = brandMapper.toBrandResponse(brandModel);

        log.info("Finish: Created Brand by request body :{} With successfully!",CommonUtils.toJsonString(brandResponse));
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<BrandResponse> getById(UUID id) {
        log.info("Start: Getting Brand by id {}", id);

        // call getById from domain for validate not found id
        BrandModel brandById = brandDomainRepo.getById(id.toString()).orElseThrow(()->{
            log.error("brand id {} is not found !",id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,String.format("brand id %s is not found !",id));
        });

        log.info("Finish: Getting Brand by id {}", id);
        return ResponseModel.success(brandMapper.toBrandResponse(brandById));
    }

    @Override
    public ResponseModel<List<BrandResponse>> getAll() {
        log.info("Start: Getting all brand body");
        List<BrandModel> getAllBrand = brandDomainRepo.getAll();

        // map model to response class
        List<BrandResponse> responses = getAllBrand.stream()
                .map(brandMapper::toBrandResponse)
                .toList();

        log.info("Finish: Getting all brand body with successfully !");
        return ResponseModel.success(responses);
    }

    @Override
    public ResponseModel<BrandResponse> update(BrandRequest brandRequest, String id) {
        log.info("Start: Updating Brand ID: {} with new name: {}", id, brandRequest.getBrandName());

        BrandModel brandModel = brandDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Brand not found id {}",id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Brand not found");
        });

        // Validate duplicated brand name
        if (brandModel.getBrandName().equals(brandRequest.getBrandName())) {
            log.error("Brand name : {} is already exist !", brandRequest.getBrandName());
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, String.format("Brand name %s is already exist!", brandRequest.getBrandName()));
        }

        brandDomainRepo.update(brandModel, brandRequest.getBrandName(), brandRequest.getImageUrl());
        log.info("Finish: Updating Brand was successfully !");
        return ResponseModel.success();
    }

    @Override
    @Transactional
    public ResponseModel<String> uploadImage(UUID brandId, MultipartFile file) {

        log.info("Start: Uploading image for brand ID: {}", brandId);

        //  Validate brand exists
        BrandModel brand = brandDomainRepo.getById(brandId.toString())
                .orElseThrow(() -> {
                    log.error("Brand id {} not found", brandId);
                    return new ApiException(
                            ResponseConstants.ResponseStatus.NOT_FOUND,
                            "Brand not found"
                    );
                });

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Image file must not be empty"
            );
        }

        //  Keep old image id
        String oldImageId = brand.getImageUrl();

        try {
            //  Upload new image using Attachment Service
            ResponseModel<String> attachmentResponse =
                    attachmentClient.uploadImage("products", file);

            if (attachmentResponse == null || attachmentResponse.getData() == null) {
                log.error("Image upload failed for brand {}", brandId);
                throw new ApiException("Image upload failed");
            }

            String newImageId = attachmentResponse.getData();

            // Update DB from repo
            brandDomainRepo.updateBrandImage(brandId, newImageId);

            //  Delete old image safely
            if (oldImageId != null) {
                try {
                    attachmentClient.deleteImage("products", oldImageId);
                } catch (Exception e) {
                    log.warn("Failed to delete old brand image: {}", oldImageId);
                }
            }

            log.info("Finish: Brand image upload successful");
            return ResponseModel.success(newImageId);

        } catch (Exception e) {
            log.error("Brand image upload failed: {}", e.getMessage());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Failed to upload brand image"
            );
        }
    }

}

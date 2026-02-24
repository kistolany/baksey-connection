package com.microservices.payment_service.domain.service.impl;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.PageResponse;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import com.microservices.product_service.application.request.CategoryRequest;
import com.microservices.product_service.application.response.CategoryResponse;
import com.microservices.product_service.domain.db_repo.BrandDomainRepo;
import com.microservices.product_service.domain.db_repo.CategoryDomainRepo;
import com.microservices.product_service.domain.mapper.CategoryMapper;
import com.microservices.product_service.domain.model.BrandModel;
import com.microservices.product_service.domain.model.CategoryModel;
import com.microservices.product_service.domain.outbound.feignclient.AttachmentClient;
import com.microservices.product_service.domain.service.BrandService;
import com.microservices.product_service.domain.service.CategoryService;
import com.microservices.product_service.infrastructure.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryImplService implements CategoryService {
    private final CategoryDomainRepo categoryDomainRepo;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final BrandService brandService;
    private final BrandDomainRepo brandDomainRepo;
    private final AttachmentClient attachmentClient;

    @Override
    public ResponseModel<CategoryResponse> create(CategoryRequest request) {
        log.info("Start: Create category by request body {}", CommonUtils.toJsonString(request));

        // validate existing brand
        String brandId = request.getBrandId().toString();
        brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found !", brandId);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Brand id : " + brandId + " is not found !");
        });

        // Validate for duplicate name in brand
        boolean isDuplicate = categoryRepository.existsByCategoryNameAndBrandUuid(request.getCategoryName(), UUID.fromString(brandId));

        if (isDuplicate) {
            log.error("Category is already exist in this brand !");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Category name = %s is already exist in brand id %s !", request.getCategoryName(), brandId));
        }

        // Call method create from repo
        categoryDomainRepo.create(request.getCategoryName(), request.getBrandId().toString());
        log.info("Finish: Create category by request body with successfully !");
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<CategoryResponse> getById(String id) {
        log.info("Start: Getting category by id {}", id);

        // Call getById method from repo and throw error when not found
        CategoryModel categoryModel = categoryDomainRepo.getById(id)
                .orElseThrow(() -> {
                    log.error("Category id : {} is not found !", id);
                    return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Category id : %s is not found !", id));
                });

        // Map model to response and return it
        log.info("Finish: Getting category by id {} with successfully", id);
        return ResponseModel.success(categoryMapper.toCategoryResponse(categoryModel));
    }

    @Override
    public ResponseModel<PageResponse<CategoryResponse>> getAll(PageRequest pageRequest) {
        log.info("Start: Getting Paged Categories");

        // 2. Call Repo method
        Page<CategoryModel> pageModel = categoryDomainRepo.getAll(pageRequest.toPageable());

        // 3. Map List<CategoryModel> -> List<CategoryResponse>
        List<CategoryResponse> categoryResponses = pageModel.getContent().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();

        // 4. Return new PageResponse with mapped list and existing pagination
        log.info("Finish: Getting Category with successfully !");
        return ResponseModel.success(PageResponse.fromPage(pageModel, categoryResponses));

    }

    @Override
    public ResponseModel<CategoryResponse> update(CategoryRequest categoryRequest, String id) {
        log.info("Start: Updating category id : {}", id);

        // Call method for not found category id
        CategoryModel categoryModel = categoryDomainRepo.getById(id).orElseThrow(() -> {
            log.error("Category id : {} is not found!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Category id : " + id + " is not found !");
        });

        // validate existing brand
        String brandId = categoryRequest.getBrandId().toString();
        BrandModel brandModel = brandDomainRepo.getById(brandId).orElseThrow(() -> {
            log.error("Brand id : {} is not found!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Brand id : " + id + " is not found !");
        });

        // Validate for duplicate name in brand
        boolean isDuplicateName = categoryModel.getCategoryName().equals(categoryRequest.getCategoryName());
        boolean isDuplicateBrandId = brandModel.getUuid().equals(categoryRequest.getBrandId());

        if (isDuplicateName && isDuplicateBrandId) {
            log.error("Category is already exist in this brand  !");
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Category name = %s is already exist in brand id %s !", categoryRequest.getCategoryName(), categoryRequest.getBrandId()));
        }

        // Call update method from repo
        categoryDomainRepo.update(
                categoryRequest.getCategoryName(),
                brandId,
                id);

        log.info("Finish: Updating category id : {} with successfully !", id);
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<List<CategoryResponse>> getAllByBrandUuid(String BrandUuid) {
        log.info("Start: Getting all Category by Brand id: {}", BrandUuid);

        // call method get by brand id from rep
        List<CategoryModel> allByBrandUuid = categoryDomainRepo.getAllByBrandUuid(BrandUuid);

        // map model to response
        List<CategoryResponse> responses = allByBrandUuid.stream().map(categoryMapper::toCategoryResponse).toList();

        log.info("Finish: Getting all category by brand id: {} successfully !", BrandUuid);
        return ResponseModel.success(responses);
    }


    @Override
    @Transactional
    public ResponseModel<String> uploadImage(UUID categoryId, MultipartFile file) {

        log.info("Start: Uploading image for category ID: {}", categoryId);

        // Validate category exists
        CategoryModel category = categoryDomainRepo.getById(categoryId.toString())
                .orElseThrow(() -> {
                    log.error("Category id {} not found", categoryId);
                    return new ApiException(
                            ResponseConstants.ResponseStatus.NOT_FOUND,
                            "Category not found"
                    );
                });

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Image file must not be empty"
            );
        }

        // Keep old image path
        String oldImagePath = category.getImageUrl();

        try {
            // Upload new image
            ResponseModel<String> attachmentServiceResponse = attachmentClient.uploadImage("products", file);
            if (attachmentServiceResponse == null) {
                throw new ApiException("Image upload failed");
            }

            String newImageId = attachmentServiceResponse.getData();

            // Update DB
            categoryDomainRepo.updateCategoryImage(categoryId, newImageId);

            // Delete old image (safe delete)
            if (oldImagePath != null) {
                try {
                    attachmentClient.deleteImage("products", oldImagePath);
                } catch (Exception ex) {
                    log.warn("Failed to delete old category image: {}", oldImagePath);
                }
            }

            log.info("Finish: Category image upload successful");
            return ResponseModel.success(newImageId);

        } catch (Exception e) {
            log.error("Category image upload failed: {}", e.getMessage());
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Failed to upload category image"
            );
        }
    }

}

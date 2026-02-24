package com.microservices.product_service.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.microservices.product_service.application.request.CategoryRequest;
import com.microservices.product_service.application.response.CategoryResponse;
import com.microservices.product_service.domain.model.CategoryModel;
import com.microservices.product_service.domain.service.BrandService;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", uses = {BrandService.class})
public interface CategoryMapper {

 //   @Mapping(target = "imageUrl", source = "imageUrl", qualifiedByName = "fileToString")
    CategoryModel toCategoryModel(CategoryRequest categoryRequest);

    CategoryResponse toCategoryResponse(CategoryModel categoryModel);

    List<CategoryResponse> toResponeList(List<CategoryModel> categoryEntities);

}

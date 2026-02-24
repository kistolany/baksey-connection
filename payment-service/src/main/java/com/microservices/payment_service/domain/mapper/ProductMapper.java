package com.microservices.payment_service.domain.mapper;

import com.microservices.product_service.application.request.ProductRequest;
import com.microservices.product_service.application.response.ProductResponse;
import com.microservices.product_service.domain.model.ProductModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

   // @Mapping(target = "imagePath", source = "image", qualifiedByName = "fileToString")
    ProductModel toProductModel(ProductRequest productRequest);

    ProductResponse toProductResponse(ProductModel productModel);

    List<ProductResponse> toResponeList(List<ProductModel> productModels);

//    @Named("fileToString")
//    default String fileToString(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            return null;
//        }
//        return file.getOriginalFilename();
//    }
}

package com.microservices.inventory_service.domain.mapper;

import com.microservices.inventory_service.application.request.InventoryRequest;
import com.microservices.inventory_service.application.respone.InventoryResponse;
import com.microservices.inventory_service.domain.model.InventoryModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "imagePath", source = "imagePath", qualifiedByName = "stringToList")
    InventoryResponse toInventoryResponse(InventoryModel inventoryModel);

    InventoryModel toInventoryModel(InventoryRequest inventoryRequest);

    // Custom logic to wrap the String into a List
    @Named("stringToList")
    default List<String> stringToList(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(imagePath);
    }
}

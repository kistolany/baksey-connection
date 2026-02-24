package com.microservices.inventory_service.infrastructure.repository.repoMapper;
import com.microservices.inventory_service.domain.model.InventoryModel;
import com.microservices.inventory_service.infrastructure.repository.entity.InventoryEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",builder = @Builder(disableBuilder = true))
public interface InventoryRepoMapper {
    @Mapping(source = "inventoryId", target = "uuid")
    InventoryEntity toInventoryEntity(InventoryModel model);

    @Mapping(source = "uuid", target = "inventoryId")
    InventoryModel toInventoryModel(InventoryEntity entity);
}

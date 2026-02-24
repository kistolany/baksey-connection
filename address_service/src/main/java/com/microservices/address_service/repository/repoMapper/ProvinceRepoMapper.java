package com.microservices.address_service.repository.repoMapper;

import com.microservices.address_service.domain.model.ProvinceModel;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProvinceRepoMapper {

    ProvinceEntity toProvinceEntity(ProvinceModel model);

    ProvinceModel toProvinceModel(ProvinceEntity entity);
}

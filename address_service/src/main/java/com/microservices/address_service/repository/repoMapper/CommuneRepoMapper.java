package com.microservices.address_service.repository.repoMapper;

import com.microservices.address_service.domain.model.CommuneModel;
import com.microservices.address_service.domain.model.ProvinceModel;
import com.microservices.address_service.repository.entity.CommuneEntity;
import com.microservices.address_service.repository.entity.DistrictEntity;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommuneRepoMapper {
    @Mapping(target = "district", source = "districtId")
    CommuneEntity tCommuneEntity(CommuneModel model);

    @Mapping(target = "districtId", source = "district.uuid")
    CommuneModel tCommuneModel(CommuneEntity entity);

    default DistrictEntity mapUuidToDistrict(UUID districtId) {
        if (districtId == null) return null;
        DistrictEntity district = new DistrictEntity();
        district.setUuid(districtId);
        return district;
    }
}

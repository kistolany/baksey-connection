package com.microservices.address_service.repository.repoMapper;

import com.microservices.address_service.domain.model.DistrictModel;
import com.microservices.address_service.repository.entity.DistrictEntity;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DistrictRepoMapper {

    @Mapping(target = "province", source = "provinceId")
    DistrictEntity tDistrictEntity(DistrictModel model);

    @Mapping(target = "provinceId", source = "province.uuid")
    @Mapping(target = "provinceNameKh", source = "province.nameKh")
    @Mapping(target = "provinceNameEn", source = "province.nameEn")
    DistrictModel tDistrictModel(DistrictEntity entity);

    default ProvinceEntity mapUuidToProvince(UUID provinceId) {
        if (provinceId == null) return null;
        ProvinceEntity province = new ProvinceEntity();
        province.setUuid(provinceId);
        return province;
    }
}

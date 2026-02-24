package com.microservices.address_service.repository.repoMapper;

import com.microservices.address_service.domain.model.AddressModel;
import com.microservices.address_service.repository.entity.AddressEntity;
import com.microservices.address_service.repository.entity.CommuneEntity;
import com.microservices.address_service.repository.entity.DistrictEntity;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AddressRepoMapper {

    // --- Entity to Model (FETCH NAMES) ---
    @Mapping(target = "provinceId", source = "province.uuid")
    @Mapping(target = "provinceNameEn", source = "province.nameEn")
    @Mapping(target = "provinceNameKh", source = "province.nameKh")

    @Mapping(target = "districtId", source = "district.uuid")
    @Mapping(target = "districtNameEn", source = "district.nameEn")
    @Mapping(target = "districtNameKh", source = "district.nameKh")

    @Mapping(target = "communeId", source = "commune.uuid")
    @Mapping(target = "communeNameEn", source = "commune.nameEn")
    @Mapping(target = "communeNameKh", source = "commune.nameKh")
    AddressModel toAddressModel(AddressEntity entity);

    // --- Model to Entity (SET IDs) ---
    @Mapping(target = "province", source = "provinceId", qualifiedByName = "mapUuidToProvince")
    @Mapping(target = "district", source = "districtId", qualifiedByName = "mapUuidToDistrict")
    @Mapping(target = "commune", source = "communeId", qualifiedByName = "mapUuidToCommune")
    AddressEntity toAddressEntity(AddressModel model);

    @Named("mapUuidToProvince")
    default ProvinceEntity mapUuidToProvince(UUID uuid) {
        if (uuid == null) return null;
        ProvinceEntity e = new ProvinceEntity();
        e.setUuid(uuid);
        return e;
    }

    @Named("mapUuidToDistrict")
    default DistrictEntity mapUuidToDistrict(UUID uuid) {
        if (uuid == null) return null;
        DistrictEntity e = new DistrictEntity();
        e.setUuid(uuid);
        return e;
    }

    @Named("mapUuidToCommune")
    default CommuneEntity mapUuidToCommune(UUID uuid) {
        if (uuid == null) return null;
        CommuneEntity e = new CommuneEntity();
        e.setUuid(uuid);
        return e;
    }
}
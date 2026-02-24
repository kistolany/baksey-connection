package com.microservices.address_service.domain.db_repo;

import com.microservices.address_service.domain.model.DistrictModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DistrictDomainRepo {

        List<DistrictModel> findAll();

        Optional<DistrictModel> findById(UUID id);

        List<DistrictModel> findByProvinceId(UUID provinceId);

        DistrictModel save(UUID provinceId, String nameKh, String nameEg);

        DistrictModel update(DistrictModel model, UUID provinceId, String nameKh, String nameEn);
}

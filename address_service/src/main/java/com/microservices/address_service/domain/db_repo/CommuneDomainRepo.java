package com.microservices.address_service.domain.db_repo;

import com.microservices.address_service.domain.model.CommuneModel;
import com.microservices.address_service.domain.model.ProvinceModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommuneDomainRepo {

    List<CommuneModel> findAll();

    Optional<CommuneModel> findById(UUID id);

    List<CommuneModel> findByDistrictId(UUID districtId);

    CommuneModel save(UUID districtId, String nameEn, String nameKh);

    CommuneModel update(CommuneModel model, UUID districtId, String nameEn, String nameKh);
}



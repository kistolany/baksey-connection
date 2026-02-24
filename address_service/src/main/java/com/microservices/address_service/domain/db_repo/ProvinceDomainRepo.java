package com.microservices.address_service.domain.db_repo;

import com.microservices.address_service.domain.model.ProvinceModel;
import com.microservices.address_service.repository.entity.ProvinceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvinceDomainRepo {
    List<ProvinceModel> findAll();

    Optional<ProvinceModel> findById(UUID id);

    Optional<ProvinceModel> findByCode(String code);

    ProvinceModel save(String nameEn,String nameKh, String code);
}

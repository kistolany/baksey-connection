package com.microservices.address_service.repository;

import com.microservices.address_service.repository.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DistrictRepository extends JpaRepository<DistrictEntity, UUID> {

    boolean existsByNameKhAndProvinceUuid(String nameKh, UUID provinceId);
    boolean existsByNameEnAndProvinceUuid(String nameEn, UUID provinceId);

    List<DistrictEntity> findByProvinceUuid(UUID provinceId);
}



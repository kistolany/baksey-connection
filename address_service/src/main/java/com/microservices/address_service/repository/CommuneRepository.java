package com.microservices.address_service.repository;

import com.microservices.address_service.repository.entity.CommuneEntity;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommuneRepository extends JpaRepository<CommuneEntity, UUID> {
    // Used for FindByDistrict
    List<CommuneEntity> findByDistrictUuid(UUID districtId);

    // Used for validation during SAVE (check if name exists in this district)
    boolean existsByNameKhAndDistrictUuid(String nameKh, UUID districtId);
    boolean existsByNameEnAndDistrictUuid(String nameEn, UUID districtId);
}


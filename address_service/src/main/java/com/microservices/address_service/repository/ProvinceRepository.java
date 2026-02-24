package com.microservices.address_service.repository;

import com.microservices.address_service.repository.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, UUID> {
    Optional<ProvinceEntity> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByNameEn(String nameEn);
    boolean existsByNameKh(String nameKh);
}

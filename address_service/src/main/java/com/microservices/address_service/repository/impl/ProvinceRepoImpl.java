package com.microservices.address_service.repository.impl;

import com.microservices.address_service.domain.db_repo.ProvinceDomainRepo;
import com.microservices.address_service.domain.model.ProvinceModel;
import com.microservices.address_service.repository.ProvinceRepository;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import com.microservices.address_service.repository.repoMapper.ProvinceRepoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class ProvinceRepoImpl implements ProvinceDomainRepo {
    private final ProvinceRepository provinceRepository;
    private final ProvinceRepoMapper repoMapper;

    @Override
    public List<ProvinceModel> findAll() {
        return provinceRepository.findAll().stream().map(repoMapper::toProvinceModel).toList();
    }

    @Override
    public Optional<ProvinceModel> findById(UUID id) {
        return provinceRepository.findById(id).map(repoMapper::toProvinceModel);
    }

    @Override
    public Optional<ProvinceModel> findByCode(String code) {
        return provinceRepository.findByCode(code).map(repoMapper::toProvinceModel);
    }

    @Override
    public ProvinceModel save(String nameEn, String nameKh, String code) {
        ProvinceEntity entity = ProvinceEntity.builder()
                .nameEn(nameEn)
                .nameKh(nameKh)
                .code(code)
                .build();
       return repoMapper.toProvinceModel(provinceRepository.save(entity));
    }
}

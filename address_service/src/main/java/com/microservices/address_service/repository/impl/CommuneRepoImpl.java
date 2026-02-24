package com.microservices.address_service.repository.impl;

import com.microservices.address_service.domain.db_repo.CommuneDomainRepo;
import com.microservices.address_service.domain.model.CommuneModel;
import com.microservices.address_service.repository.CommuneRepository;
import com.microservices.address_service.repository.entity.CommuneEntity;
import com.microservices.address_service.repository.repoMapper.CommuneRepoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@AllArgsConstructor
@Component
public class CommuneRepoImpl implements CommuneDomainRepo {
    private final CommuneRepository communeRepository;
    private final CommuneRepoMapper repoMapper;

    @Override
    public List<CommuneModel> findAll() {
        return communeRepository.findAll().stream().map(repoMapper::tCommuneModel).toList();
    }

    @Override
    public Optional<CommuneModel> findById(UUID id) {
        return communeRepository.findById(id).map(repoMapper::tCommuneModel);
    }

    @Override
    public List<CommuneModel> findByDistrictId(UUID districtId) {
        return communeRepository.findByDistrictUuid(districtId).stream()
                .map(repoMapper::tCommuneModel).toList();
    }

    @Override
    public CommuneModel save(UUID districtId, String nameEn, String nameKh) {
        CommuneModel model = CommuneModel.builder()
                .districtId(districtId)
                .nameEn(nameEn)
                .nameKh(nameKh)
                .build();
        return repoMapper.tCommuneModel(communeRepository.save(repoMapper.tCommuneEntity(model)));
    }

    @Override
    public CommuneModel update(CommuneModel model, UUID districtId, String nameEn, String nameKh) {
        model.setDistrictId(districtId);
        model.setNameEn(nameEn);
        model.setNameKh(nameKh);

        CommuneEntity entity = repoMapper.tCommuneEntity(model);
        // Force the ID to ensure an UPDATE happens, not an INSERT
        entity.setUuid(model.getUuid());

        return repoMapper.tCommuneModel(communeRepository.save(entity));
    }
}

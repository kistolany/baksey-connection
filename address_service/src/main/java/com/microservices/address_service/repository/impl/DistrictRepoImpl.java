package com.microservices.address_service.repository.impl;

import com.microservices.address_service.domain.db_repo.DistrictDomainRepo;
import com.microservices.address_service.domain.model.DistrictModel;
import com.microservices.address_service.repository.DistrictRepository;
import com.microservices.address_service.repository.entity.DistrictEntity;
import com.microservices.address_service.repository.repoMapper.DistrictRepoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class DistrictRepoImpl implements DistrictDomainRepo {
    private final DistrictRepository districtRepository;
    private final DistrictRepoMapper districtMapper;

    @Override
    public List<DistrictModel> findAll() {
        return districtRepository.findAll().stream()
                .map(districtMapper::tDistrictModel)
                .toList();
    }

    @Override
    public Optional<DistrictModel> findById(UUID id) {
        return districtRepository.findById(id)
                .map(districtMapper::tDistrictModel);
    }

    @Override
    public List<DistrictModel> findByProvinceId(UUID provinceId) {
        return districtRepository.findByProvinceUuid(provinceId).stream()
                .map(districtMapper::tDistrictModel)
                .toList();
    }

    @Override
    public DistrictModel save(UUID provinceId, String nameKh, String nameEn) {
        DistrictModel model = DistrictModel.builder()
                .provinceId(provinceId)
                .nameKh(nameKh)
                .nameEn(nameEn)
                .build();

        // Map to entity
        DistrictEntity entity = districtMapper.tDistrictEntity(model);

        // Save Entity and map back to Model
        DistrictEntity savedEntity = districtRepository.save(entity);
        return districtMapper.tDistrictModel(savedEntity);
    }

        @Override
        public DistrictModel update(DistrictModel model, UUID provinceId, String nameKh, String nameEn) {

            model.setProvinceId(provinceId);
            model.setNameKh(nameKh);
            model.setNameEn(nameEn);

            // map to entity
            DistrictEntity saveEntity = districtMapper.tDistrictEntity(model);
            saveEntity.setUuid(model.getUuid());

            // 4. Save and map back to model
            DistrictEntity updatedEntity = districtRepository.save(saveEntity);
            return districtMapper.tDistrictModel(updatedEntity);
        }

}
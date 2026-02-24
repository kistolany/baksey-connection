package com.microservices.address_service.domain.service.impl;

import com.microservices.address_service.appicatioin.request.DistrictRequest;
import com.microservices.address_service.appicatioin.response.DistrictResponse;
import com.microservices.address_service.domain.db_repo.DistrictDomainRepo;
import com.microservices.address_service.domain.db_repo.ProvinceDomainRepo;
import com.microservices.address_service.domain.mapper.DistrictMapper;
import com.microservices.address_service.domain.model.DistrictModel;
import com.microservices.address_service.domain.service.DistrictService;
import com.microservices.address_service.repository.DistrictRepository;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class DistrictServiceImpl implements DistrictService {

    private final DistrictDomainRepo districtDomainRepo;
    private final DistrictMapper districtMapper;
    private final ProvinceDomainRepo provinceDomainRepo;
    private final DistrictRepository districtRepository;

    @Override
    public ResponseModel<List<DistrictResponse>> findAll() {
        log.info("Start: Getting  District with body");

        // Call repo for find all
        List<DistrictResponse> list = districtDomainRepo.findAll().stream()
                .map(districtMapper::tDistrictResponse)
                .toList();

        log.info("Start: Getting  District with body ");
        return ResponseModel.success(list);
    }

    @Override
    public ResponseModel<DistrictResponse> save(DistrictRequest request) {

        log.info("Start: Creating District with body: {}",
                CommonUtils.toJsonString(request));

        // 1️⃣ Validate Province
        var province = provinceDomainRepo.findById(request.getProvinceId())
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "Province not found!"));

        // 2️⃣ Validate duplicates
        if (districtRepository.existsByNameKhAndProvinceUuid(
                request.getNameKh(), request.getProvinceId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "District nameKh already exists!"
            );
        }

        if (districtRepository.existsByNameEnAndProvinceUuid(
                request.getNameEn(), request.getProvinceId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "District nameEn already exists!"
            );
        }

        // 3️⃣ Save
        DistrictModel savedModel = districtDomainRepo.save(
                request.getProvinceId(),
                request.getNameKh(),
                request.getNameEn()
        );

        // 4️⃣ 🔥 SET PROVINCE NAME INTO MODEL
        savedModel.setProvinceNameEn(province.getNameEn());
        savedModel.setProvinceNameKh(province.getNameKh());

        log.info("Finish: Created District successfully");

        return ResponseModel.success(
                districtMapper.tDistrictResponse(savedModel)
        );
    }

    @Override
    public ResponseModel<List<DistrictResponse>> findByProvinceId(UUID provinceId) {
        log.info("Start: Getting District with province id: {}", provinceId);

        // Validate province id
        provinceDomainRepo.findById(provinceId).orElseThrow(() -> {
            log.error("Province id :{} is not found!", provinceId);
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Province not found !");
        });

        List<DistrictResponse> list = districtDomainRepo.findByProvinceId(provinceId).stream()
                .map(districtMapper::tDistrictResponse)
                .toList();

        log.info("Finish: Getting  District by id : {} successfully",provinceId);
        return ResponseModel.success(list);
    }

    @Override
    public ResponseModel<DistrictResponse> findById(UUID id) {
        log.info("Start: Getting District by id : {}", id);

        // Validate exist name
        DistrictModel model = districtDomainRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("District id {} not found",id);
                   throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "District not found");
                });

        log.info("Finish: Getting  by id : {} successfully",id);
        return ResponseModel.success(districtMapper.tDistrictResponse(model));
    }

    @Override
    public ResponseModel<DistrictResponse> update(UUID id, DistrictRequest request) {

        log.info("Start: Updating District ID: {} with body: {}",
                id, CommonUtils.toJsonString(request));

        // 1️⃣ Validate District exists
        DistrictModel existing = districtDomainRepo.findById(id)
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "District not found!"));

        // 2️⃣ Validate Province exists
        provinceDomainRepo.findById(request.getProvinceId())
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "Province not found!"));

        // 3️⃣ Validate duplicate nameKh (exclude current district)
        if (!existing.getNameKh().equals(request.getNameKh())
                && districtRepository.existsByNameKhAndProvinceUuid(
                request.getNameKh(), request.getProvinceId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "District nameKh already exists!"
            );
        }

        // 4️⃣ Validate duplicate nameEn
        if (!existing.getNameEn().equals(request.getNameEn())
                && districtRepository.existsByNameEnAndProvinceUuid(
                request.getNameEn(), request.getProvinceId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "District nameEn already exists!"
            );
        }

        // 5️⃣ Update
        DistrictModel updatedModel = districtDomainRepo.update(
                existing,
                request.getProvinceId(),
                request.getNameKh(),
                request.getNameEn()
        );

        log.info("Finish: Updated District successfully");

        // ✅ Return updated data
        return ResponseModel.success(
                districtMapper.tDistrictResponse(updatedModel)
        );
    }
}
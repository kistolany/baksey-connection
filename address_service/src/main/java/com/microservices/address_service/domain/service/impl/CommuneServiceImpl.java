package com.microservices.address_service.domain.service.impl;

import com.microservices.address_service.appicatioin.request.CommuneRequest;
import com.microservices.address_service.appicatioin.response.CommuneResponse;
import com.microservices.address_service.domain.db_repo.CommuneDomainRepo;
import com.microservices.address_service.domain.db_repo.DistrictDomainRepo;
import com.microservices.address_service.domain.mapper.CommuneMapper;
import com.microservices.address_service.domain.model.CommuneModel;
import com.microservices.address_service.domain.service.CommuneService;
import com.microservices.address_service.repository.CommuneRepository;
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
public class CommuneServiceImpl implements CommuneService {
    private final CommuneMapper communeMapper;
    private final CommuneDomainRepo communeDomainRepo;
    private final DistrictDomainRepo districtDomainRepo;
    private final CommuneRepository communeRepository;

    @Override
    public ResponseModel<List<CommuneResponse>> findAll() {
        log.info("Start: Getting all Communes");
        List<CommuneResponse> list = communeDomainRepo.findAll().stream().map(communeMapper::tCommuneResponse).toList();

        log.info("Finish: Getting all Communes with successfully");
        return ResponseModel.success(list);
    }

    @Override
    public ResponseModel<CommuneResponse> findById(UUID id) {
        log.info("Start: Finding Commune by id: {}", id);
        CommuneModel model = communeDomainRepo.findById(id).orElseThrow(() -> new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, "Commune not found!"));

        log.info("Finish: Finding Commune by id: {} with successfully", id);
        return ResponseModel.success(communeMapper.tCommuneResponse(model));
    }

    @Override
    public ResponseModel<List<CommuneResponse>> findByDistrictId(UUID districtId) {
        log.info("Start: Finding Communes by District ID: {}", districtId);
        List<CommuneResponse> list = communeDomainRepo.findByDistrictId(districtId).stream().map(communeMapper::tCommuneResponse).toList();

        log.info("Finish: Finding Communes by District ID: {}", districtId);
        return ResponseModel.success(list);
    }

    @Override
    public ResponseModel<CommuneResponse> save(CommuneRequest request) {

        log.info("Start: Creating Commune body: {}",
                CommonUtils.toJsonString(request));

        // Validate District exists
        var district = districtDomainRepo.findById(request.getDistrictId())
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "District ID not found!")
                );

        //  Validate duplicate Khmer name
        if (communeRepository.existsByNameKhAndDistrictUuid(
                request.getNameKh(), request.getDistrictId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Commune name (KH) already exists in this district!"
            );
        }

        //  Validate duplicate English name
        if (communeRepository.existsByNameEnAndDistrictUuid(
                request.getNameEn(), request.getDistrictId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "Commune name (EN) already exists in this district!"
            );
        }

        //  Save
        CommuneModel savedModel = communeDomainRepo.save(
                request.getDistrictId(),
                request.getNameEn(),
                request.getNameKh()
        );

        log.info("Finish: Created Commune successfully");

        //  RETURN DATA
        return ResponseModel.success(
                communeMapper.tCommuneResponse(savedModel)
        );
    }

    @Override
    public ResponseModel<CommuneResponse> update(UUID id, CommuneRequest request) {

        log.info("Start: Updating Commune ID: {} with new data: {}",
                id, CommonUtils.toJsonString(request));

        //  Validate Commune exists
        CommuneModel existing = communeDomainRepo.findById(id)
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "Commune not found")
                );

        //  Validate District exists
        var district = districtDomainRepo.findById(request.getDistrictId())
                .orElseThrow(() ->
                        new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                                "District not found!")
                );

        //  Validate Khmer name change
        if (!existing.getNameKh().equals(request.getNameKh())
                && communeRepository.existsByNameKhAndDistrictUuid(
                request.getNameKh(), request.getDistrictId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "New Khmer name already exists!"
            );
        }

        //  Validate English name change
        if (!existing.getNameEn().equals(request.getNameEn())
                && communeRepository.existsByNameEnAndDistrictUuid(
                request.getNameEn(), request.getDistrictId())) {

            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    "New English name already exists!"
            );
        }

        //  Update
        CommuneModel updated = communeDomainRepo.update(
                existing,
                request.getDistrictId(),
                request.getNameEn(),
                request.getNameKh()
        );


        log.info("Finish: Updated Commune successfully");

        return ResponseModel.success(
                communeMapper.tCommuneResponse(updated)
        );
    }
}
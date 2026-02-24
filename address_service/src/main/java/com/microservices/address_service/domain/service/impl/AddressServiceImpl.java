package com.microservices.address_service.domain.service.impl;

import com.microservices.address_service.appicatioin.request.AddressRequest;
import com.microservices.address_service.appicatioin.response.AddressResponse;
import com.microservices.address_service.appicatioin.response.CommuneResponse;
import com.microservices.address_service.appicatioin.response.DistrictResponse;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.domain.db_repo.AddressDomainRepo;
import com.microservices.address_service.domain.db_repo.CommuneDomainRepo;
import com.microservices.address_service.domain.db_repo.DistrictDomainRepo;
import com.microservices.address_service.domain.db_repo.ProvinceDomainRepo;
import com.microservices.address_service.domain.mapper.AddressMapper;
import com.microservices.address_service.domain.model.AddressModel;
import com.microservices.address_service.domain.service.AddressService;
import com.microservices.address_service.repository.AddressRepository;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;
    private final AddressDomainRepo addressDomainRepo;
    private final AddressRepository addressRepository;

    private final ProvinceDomainRepo provinceDomainRepo;
    private final DistrictDomainRepo districtDomainRepo;
    private final CommuneDomainRepo communeDomainRepo;

    @Override
    public ResponseModel<List<AddressResponse>> findAll() {
        log.info("Start: Getting Address list");

        List<AddressResponse> listResponse = addressDomainRepo.findAll()
                .stream()
                .map(addressMapper::toAddressResponse)
                .toList();

        log.info("Finish: Getting Address list: {} with successfully", CommonUtils.toJsonString(listResponse));
        return ResponseModel.success(listResponse);
    }

    @Override
    public ResponseModel<AddressResponse> findById(UUID id) {
        log.info("Start: Getting Address with id: {}", id);

        AddressModel model = addressDomainRepo.findById(id).orElseThrow(() -> {
            log.error("Address id: {} is not found!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    String.format("Address id: %s is not found!", id));
        });

        AddressResponse response = addressMapper.toAddressResponse(model);
        log.info("Finish: Getting Address with id: {} with successfully", id);
        return ResponseModel.success(response);
    }

    @Override
    public ResponseModel<AddressResponse> create(AddressRequest request) {
        log.info("Start: Creating Address with body: {}", CommonUtils.toJsonString(request));

        // 1. Validate Location (Province, District, Commune) using the helper
        validateLocation(request);

        // 2. Validate Default Address logic
        if (Boolean.TRUE.equals(request.getIsDefault()) &&
                addressRepository.existsByCustomerIdAndIsDefaultTrue(request.getCustomerId())) {
            log.error("Customer {} already has a default address!", request.getCustomerId());
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Customer %s already has a default address!", request.getCustomerId()));
        }

        // 3. Map Request to Domain Model
        AddressModel modelToSave = addressMapper.toAddressModel(request);

        // 4. Save via Domain Repo and Map back to Response
        AddressModel savedModel = addressDomainRepo.save(modelToSave);
        AddressResponse response = addressMapper.toAddressResponse(savedModel);
        enrichLocation(response);

        log.info("Finish: Saved Address with body: {} with successfully", CommonUtils.toJsonString(response));
        return ResponseModel.success(response);
    }

    @Override
    public ResponseModel<Void> delete(UUID id) {
        log.info("Start: Deleting Address with id: {}", id);

        if (!addressRepository.existsById(id)) {
            log.error("Address id: {} not found for deletion!", id);
            throw new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND,
                    String.format("Address id: %s is not found!", id));
        }

        addressDomainRepo.deleteById(id);

        log.info("Finish: Deleted Address with id: {} successfully", id);
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<AddressResponse> update(UUID id, AddressRequest request) {
        log.info("Start: Updating Address with id: {} and body: {}", id, CommonUtils.toJsonString(request));

        // 1. Validate if Address exists
        addressDomainRepo.findById(id).orElseThrow(() -> {
            log.error("Address id: {} is not found for update!", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Address id: %s is not found!", id));
        });

        // 2. Validate Location (Province, District, Commune) with internal logging
        validateLocation(request);

        // 3. Map and preserve the ID
        AddressModel modelToUpdate = addressMapper.toAddressModel(request);
        modelToUpdate.setUuid(id);

        AddressModel updatedModel = addressDomainRepo.save(modelToUpdate);
        AddressResponse response = addressMapper.toAddressResponse(updatedModel);
        enrichLocation(response);

        // Consistent finish log with JSON body
        log.info("Finish: Updated Address with id: {} with successfully: {}", id, CommonUtils.toJsonString(response));
        return ResponseModel.success(response);
    }
    @Override
    public ResponseModel<List<AddressResponse>> getAddressesByIds(List<UUID> ids) {
        log.info("Start: Fetching address responses for {} ids", ids.size());

        // 1. Fetch address models
        List<AddressModel> models = addressDomainRepo.findAddressIds(ids);

        if (models.isEmpty()) {
            log.info("Service: FINISH - No addresses found.");
            return ResponseModel.success(Collections.emptyList());
        }

        // 2. Map Models directly to Responses
        List<AddressResponse> responses = models.stream()
                .map(addressMapper::toAddressResponse)
                .toList();

        log.info("Finish:  Successfully returned {} enriched addresses", responses.size());
        return ResponseModel.success(responses);
    }


    /**
     * Private helper with full error logging to match your style
     */
    private void validateLocation(AddressRequest request) {
        provinceDomainRepo.findById(request.getProvinceId()).orElseThrow(() -> {
            log.error("Province id : {} is not found !", request.getProvinceId());
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Province id : %s is not found !", request.getProvinceId()));
        });

        districtDomainRepo.findById(request.getDistrictId()).orElseThrow(() -> {
            log.error("District id : {} is not found !", request.getDistrictId());
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("District id : %s is not found !", request.getDistrictId()));
        });

        communeDomainRepo.findById(request.getCommuneId()).orElseThrow(() -> {
            log.error("Commune id : {} is not found !", request.getCommuneId());
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Commune id : %s is not found !", request.getCommuneId()));
        });
    }


    // method to enrich the response with location names
    private void enrichLocation(AddressResponse response) {

        provinceDomainRepo.findById(response.getProvinceId())
                .ifPresent(province -> {
                    response.setProvinceNameEn(province.getNameEn());
                    response.setProvinceNameKh(province.getNameKh());
                });

        districtDomainRepo.findById(response.getDistrictId())
                .ifPresent(district -> {
                    response.setDistrictNameEn(district.getNameEn());
                    response.setDistrictNameKh(district.getNameKh());
                });

        communeDomainRepo.findById(response.getCommuneId())
                .ifPresent(commune -> {
                    response.setCommuneNameEn(commune.getNameEn());
                    response.setCommuneNameKh(commune.getNameKh());
                });
    }


}
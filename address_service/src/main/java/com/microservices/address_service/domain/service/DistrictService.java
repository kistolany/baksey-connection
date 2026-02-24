package com.microservices.address_service.domain.service;

import com.microservices.address_service.appicatioin.request.DistrictRequest;
import com.microservices.address_service.appicatioin.response.DistrictResponse;
import com.microservices.common_service.domain.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface DistrictService {

    ResponseModel<List<DistrictResponse>> findAll();

    ResponseModel<DistrictResponse> findById(UUID id);

    ResponseModel<List<DistrictResponse>> findByProvinceId(UUID provinceId);

    ResponseModel<DistrictResponse> save(DistrictRequest request);

    ResponseModel<DistrictResponse> update(UUID id, DistrictRequest request);
}

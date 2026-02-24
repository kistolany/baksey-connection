package com.microservices.address_service.domain.service;

import com.microservices.address_service.appicatioin.request.CommuneRequest;
import com.microservices.address_service.appicatioin.response.CommuneResponse;
import com.microservices.common_service.domain.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface CommuneService {

    ResponseModel<List<CommuneResponse>> findAll();

    ResponseModel<CommuneResponse> findById(UUID id);

    ResponseModel<List<CommuneResponse>> findByDistrictId(UUID districtId);

    ResponseModel<CommuneResponse> save(CommuneRequest request);

    ResponseModel<CommuneResponse> update(UUID id, CommuneRequest request);
}
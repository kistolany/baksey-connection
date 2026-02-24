package com.microservices.address_service.domain.service;

import com.microservices.address_service.appicatioin.request.ProvinceRequest;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.repository.entity.ProvinceEntity;
import com.microservices.common_service.domain.PageRequest;
import com.microservices.common_service.domain.ResponseModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvinceService {

    ResponseModel<List<ProvinceResponse>> findAll();

    ResponseModel<ProvinceResponse> findById(UUID id);

    ResponseModel<ProvinceResponse> findByCode(String code);

    ResponseModel<ProvinceResponse> save(ProvinceRequest request);
}

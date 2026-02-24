package com.microservices.address_service.domain.service;

import com.microservices.address_service.appicatioin.request.AddressRequest;
import com.microservices.address_service.appicatioin.response.AddressResponse;
import com.microservices.common_service.domain.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    ResponseModel<List<AddressResponse>> findAll();

    ResponseModel<AddressResponse> findById(UUID id);

    ResponseModel<AddressResponse> create(AddressRequest request);

    ResponseModel<Void> delete(UUID id);

    ResponseModel<AddressResponse> update(UUID id, AddressRequest request);

    ResponseModel<List<AddressResponse>> getAddressesByIds(List<UUID> ids);
}

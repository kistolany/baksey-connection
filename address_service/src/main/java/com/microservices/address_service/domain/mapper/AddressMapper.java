package com.microservices.address_service.domain.mapper;

import com.microservices.address_service.appicatioin.request.AddressRequest;
import com.microservices.address_service.appicatioin.response.AddressResponse;
import com.microservices.address_service.domain.model.AddressModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressModel toAddressModel(AddressRequest request);

    AddressResponse toAddressResponse(AddressModel model);
}

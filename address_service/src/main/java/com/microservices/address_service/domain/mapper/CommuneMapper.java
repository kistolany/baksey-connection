package com.microservices.address_service.domain.mapper;

import com.microservices.address_service.appicatioin.request.CommuneRequest;
import com.microservices.address_service.appicatioin.response.CommuneResponse;
import com.microservices.address_service.domain.model.CommuneModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommuneMapper {

    CommuneModel tCommuneModel(CommuneRequest request);

    CommuneResponse tCommuneResponse(CommuneModel model);
}
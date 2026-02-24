package com.microservices.address_service.domain.mapper;

import com.microservices.address_service.appicatioin.request.ProvinceRequest;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.domain.model.ProvinceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {

    ProvinceModel toProvinceModel(ProvinceRequest request);

    ProvinceResponse tProvinceResponse(ProvinceModel model);
}

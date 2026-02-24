package com.microservices.address_service.domain.mapper;

import com.microservices.address_service.appicatioin.request.DistrictRequest;
import com.microservices.address_service.appicatioin.request.ProvinceRequest;
import com.microservices.address_service.appicatioin.response.DistrictResponse;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.domain.model.DistrictModel;
import com.microservices.address_service.domain.model.ProvinceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DistrictMapper {

    DistrictModel tDistrictModel(DistrictRequest request);

    @Mapping(target = "provinceNameKh", source = "provinceNameKh")
    @Mapping(target = "provinceNameEn", source = "provinceNameEn")
    DistrictResponse tDistrictResponse(DistrictModel model);
}

package com.microservices.address_service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class DistrictModel {
    private UUID uuid;
    private UUID provinceId;
    private String provinceNameKh;
    private String provinceNameEn;
    private String nameEn;
    private String nameKh;
}

package com.microservices.address_service.appicatioin.response;

import lombok.Data;
import java.util.UUID;

@Data
public class DistrictResponse {
    private UUID uuid;
    private UUID provinceId;
    private String provinceNameKh;
    private String provinceNameEn;
    private String nameEn;
    private String nameKh;
}

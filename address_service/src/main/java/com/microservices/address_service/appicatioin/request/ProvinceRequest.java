package com.microservices.address_service.appicatioin.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ProvinceRequest {
    private String nameEn;
    private String nameKh;
    private String code;
}

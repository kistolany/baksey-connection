package com.microservices.address_service.domain.model;


import lombok.Data;

import java.util.UUID;

@Data
public class ProvinceModel {
    private UUID uuid;
    private String nameEn;
    private String nameKh;
    private String code;
}

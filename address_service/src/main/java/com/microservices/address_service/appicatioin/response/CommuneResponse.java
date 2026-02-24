package com.microservices.address_service.appicatioin.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CommuneResponse {
    private UUID uuid;
    private String nameEn;
    private String nameKh;
    private UUID districtId;
}

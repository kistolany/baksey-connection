package com.microservices.address_service.appicatioin.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddressRequest {
    private UUID customerId;
    private String phone;
    private String city;
    private String streetNo;
    private String houseNo;
    private String village;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;
    private String status;
    private UUID provinceId;
    private UUID districtId;
    private UUID communeId;
}

package com.microservices.address_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressModel {
    private UUID uuid;
    private String phone;
    private String city;
    private String streetNo;
    private String houseNo;
    private String village;
    private String country;

    // Human-readable names (En/Kh)
    private String provinceNameEn;
    private String provinceNameKh;
    private String districtNameEn;
    private String districtNameKh;
    private String communeNameEn;
    private String communeNameKh;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;
    private String status;

    // Keep IDs just in case they are needed for logic
    private UUID provinceId;
    private UUID districtId;
    private UUID communeId;
}
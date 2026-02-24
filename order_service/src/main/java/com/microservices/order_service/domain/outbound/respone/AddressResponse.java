package com.microservices.order_service.domain.outbound.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private UUID uuid;
    private String phone;
    private String city;
    private String streetNo;
    private String houseNo;
    private String village;
    private String country;

    // Add these fields to match your Address Service output
    private String provinceNameEn;
    private String provinceNameKh;
    private String districtNameEn;
    private String districtNameKh;
    private String communeNameEn;
    private String communeNameKh;

    // Keep the IDs for reference
    private UUID provinceId;
    private UUID districtId;
    private UUID communeId;

    private Boolean isDefault;
    private String status;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
package com.microservices.address_service.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity extends BaseEntity {
    private UUID customerId;
    private String phone;
    private String city;
    private String streetNo;
    private String houseNo;
    private String village;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault = false;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private ProvinceEntity province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private DistrictEntity district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commune_id")
    private CommuneEntity commune;

}

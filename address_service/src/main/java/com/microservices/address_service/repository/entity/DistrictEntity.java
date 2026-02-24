package com.microservices.address_service.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "district")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictEntity extends BaseEntity {
    private String nameEn;
    private String nameKh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private ProvinceEntity province;
}
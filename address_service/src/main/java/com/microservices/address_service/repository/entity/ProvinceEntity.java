package com.microservices.address_service.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "province")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceEntity extends BaseEntity {
    private String nameEn;
    private String nameKh;
    private String code;
}
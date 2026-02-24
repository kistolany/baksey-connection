package com.microservices.address_service.domain.model;

import com.microservices.address_service.repository.entity.DistrictEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CommuneModel {
    private UUID uuid;
    private String nameEn;
    private String nameKh;
    private UUID districtId;
}

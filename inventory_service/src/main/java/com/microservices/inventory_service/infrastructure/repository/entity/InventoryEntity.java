package com.microservices.inventory_service.infrastructure.repository.entity;

import com.microservices.common_service.helpers.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "inventory")
@Entity
public class InventoryEntity extends BaseEntity {
    private UUID productId;
    private Integer availableStock;
}

package com.microservices.address_service.repository;

import com.microservices.address_service.repository.entity.AddressEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {

    /**
     * Finds all addresses associated with a specific customer
     */
    List<AddressEntity> findByCustomerId(UUID customerId);

    /**
     * Checks if a customer already has an address marked as default.
     */
    boolean existsByCustomerIdAndIsDefaultTrue(UUID customerId);

    /**
     * Create for use another service
     */
    // This replaces the manual @Query join fetch
    @EntityGraph(attributePaths = {"province", "district", "commune"})
    List<AddressEntity> findAllByUuidIn(List<UUID> uuids);

}

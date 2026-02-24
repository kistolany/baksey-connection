package com.microservices.address_service.domain.db_repo;

import com.microservices.address_service.domain.model.AddressModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressDomainRepo {
    List<AddressModel> findAll();

    Optional<AddressModel> findById(UUID id);

    List<AddressModel> findByCustomerId(UUID customerId);

    AddressModel save(AddressModel model);

    void deleteById(UUID id);

    List<AddressModel> findAddressIds(List<UUID> addressIds);
}
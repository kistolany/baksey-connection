package com.microservices.address_service.repository.impl;

import com.microservices.address_service.domain.db_repo.AddressDomainRepo;
import com.microservices.address_service.domain.model.AddressModel;
import com.microservices.address_service.repository.AddressRepository;
import com.microservices.address_service.repository.entity.AddressEntity;
import com.microservices.address_service.repository.repoMapper.AddressRepoMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AddressRepoImpl implements AddressDomainRepo {
    private final AddressRepository addressRepository;
    private final AddressRepoMapper repoMapper;

    @Override
    public List<AddressModel> findAll() {
        return addressRepository.findAll().stream().map(repoMapper::toAddressModel).toList();
    }

    @Override
    public Optional<AddressModel> findById(UUID id) {
        return addressRepository.findById(id).map(repoMapper::toAddressModel);
    }

    @Override
    public List<AddressModel> findByCustomerId(UUID customerId) {
        return addressRepository.findByCustomerId(customerId).stream().map(repoMapper::toAddressModel).toList();
    }

    @Override
    public AddressModel save(AddressModel model) {
        AddressEntity entity = repoMapper.toAddressEntity(model);
        return repoMapper.toAddressModel(addressRepository.save(entity));
    }

    @Override
    public List<AddressModel> findAddressIds(List<UUID> addressIds) {

        // 1. Fetch address using the IN clause
        List<AddressEntity> entities = addressRepository.findAllByUuidIn(addressIds);

        // 2. Map to Domain Model and return
        return entities.stream()
                .map(repoMapper::toAddressModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        addressRepository.deleteById(id);
    }
}
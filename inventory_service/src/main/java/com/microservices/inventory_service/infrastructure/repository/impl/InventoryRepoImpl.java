package com.microservices.inventory_service.infrastructure.repository.impl;

import com.microservices.inventory_service.domain.filter.FilterDTO;
import com.microservices.inventory_service.domain.filter.SearchFilterRequest;
import com.microservices.inventory_service.domain.model.InventoryModel;
import com.microservices.inventory_service.domain.db_repo.InventoryDomainRepo;
import com.microservices.inventory_service.infrastructure.repository.InventoryRepository;
import com.microservices.inventory_service.infrastructure.repository.entity.InventoryEntity;
import com.microservices.inventory_service.infrastructure.repository.repoMapper.InventoryRepoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryRepoImpl implements InventoryDomainRepo {
    private final InventoryRepository inventoryRepository;
    private final InventoryRepoMapper inventoryRepoMapper;

    @Override
    public List<InventoryModel> findStockByProductIds(List<UUID> productIds) {

        // find by product id
        List<InventoryEntity> entities = inventoryRepository.findByProductIdIn(productIds);

        // map entity to model and return
        return entities.stream()
                .map(inventoryRepoMapper::toInventoryModel)
                .toList();
    }

    @Override
    public Page<InventoryModel> listAll(FilterDTO filter, List<UUID> allowedIds, Pageable pageable) {
        log.info("Domain Repo: Fetching paged inventory with filters");

        // 1. Create the Specification with both Stock and Product ID filters
        SearchFilterRequest spec = new SearchFilterRequest(filter, allowedIds);

        // 2. Call the repository using the Specification
        Page<InventoryEntity> inventoryEntities = inventoryRepository.findAll(spec, pageable);

        // 3. Map Entity to Model
        return inventoryEntities.map(inventoryRepoMapper::toInventoryModel);
    }

    @Override
    public Optional<InventoryModel> getById(String productId) {
        return inventoryRepository.findByProductId(UUID.fromString(productId))
                .map(inventoryRepoMapper::toInventoryModel);
    }

    @Override
    @Transactional
    public InventoryModel addProduct(String productId) {

        // create object
        InventoryEntity newEntity = InventoryEntity.builder()
                .productId(UUID.fromString(productId))
                .availableStock(0)
                .build();

        // map entity to model and return
        return inventoryRepoMapper.toInventoryModel(inventoryRepository.save(newEntity));
    }

    @Override
    public InventoryModel addStock(String productId, Integer quantity) {

        // Fetch the inventory entity by product ID
        InventoryEntity inventoryEntity = inventoryRepository.findByProductId(UUID.fromString(productId)).get();

        // set the value to inventory
        inventoryEntity.setAvailableStock(quantity);

        // save entity
        InventoryEntity saved = inventoryRepository.save(inventoryEntity);

        // map to model
        return inventoryRepoMapper.toInventoryModel(saved);
    }

    @Override
    public void cutStock(String productId, Integer quantity) {

        // 1. Fetch with Lock product
        Optional<InventoryEntity> entityOpt = inventoryRepository.findWithLockByProductId(UUID.fromString(productId));

        // 3. Just Set and Save
        InventoryEntity entity = entityOpt.get();
        entity.setAvailableStock(quantity);

        log.info(" Updated: Product {} new total is {}", productId, quantity);
        inventoryRepository.save(entity);
    }

}
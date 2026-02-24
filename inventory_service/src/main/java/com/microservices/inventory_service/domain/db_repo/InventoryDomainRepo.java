package com.microservices.inventory_service.domain.db_repo;

import com.microservices.inventory_service.domain.filter.FilterDTO;
import com.microservices.inventory_service.domain.model.InventoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryDomainRepo {

    // cut stock
    void cutStock(String productId, Integer quantity);

    // find by product id
    List<InventoryModel> findStockByProductIds(List<UUID> productIds);

    // list all
    Page<InventoryModel> listAll(FilterDTO filter, List<UUID> allowedIds, Pageable pageable);

    // find per inventory
    Optional<InventoryModel> getById(String productId);

    // add product
    InventoryModel addProduct(String productId);

    // add stock by product id
    InventoryModel addStock(String productId, Integer quantity);

}

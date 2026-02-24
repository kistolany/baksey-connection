package com.microservices.inventory_service.domain.outbound.feignclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.inventory_service.domain.outbound.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "product-service", url = "${product.base.url}")
public interface ProductFeignClient {

    @GetMapping("/{id}")
    ResponseModel<ProductResponse> getProductById(@PathVariable UUID id);

    @PostMapping("/list-by-ids")
    ResponseModel<List<ProductResponse>> getProductsByIds(@RequestBody List<UUID> ids);

}

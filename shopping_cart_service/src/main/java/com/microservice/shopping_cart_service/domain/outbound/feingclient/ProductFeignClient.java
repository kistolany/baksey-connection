package com.microservice.shopping_cart_service.domain.outbound.feingclient;

import com.microservice.shopping_cart_service.domain.outbound.respone.ProductResponse;
import com.microservices.common_service.domain.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service", url = "${product.base.url}")
public interface ProductFeignClient {

    @GetMapping("/{id}")
    ResponseModel<ProductResponse> getProductById(@PathVariable UUID id);

    @PostMapping("/list-by-ids")
    ResponseModel<List<ProductResponse>> getProductsByIds(@RequestBody List<String> ids);
}

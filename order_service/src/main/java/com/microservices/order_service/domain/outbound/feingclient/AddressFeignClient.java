package com.microservices.order_service.domain.outbound.feingclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.order_service.domain.outbound.respone.AddressResponse;
import com.microservices.order_service.domain.outbound.respone.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "address-service", url = "${address.base.url}")
public interface AddressFeignClient {

    @PostMapping("/list-by-ids")
    ResponseModel<List<AddressResponse>> getAddressByIds(@RequestBody List<String> ids);

}

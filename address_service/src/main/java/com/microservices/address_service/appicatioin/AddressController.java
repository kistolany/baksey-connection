package com.microservices.address_service.appicatioin;

import com.microservices.address_service.appicatioin.request.AddressRequest;
import com.microservices.address_service.appicatioin.response.AddressResponse;
import com.microservices.address_service.domain.service.AddressService;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.utils.CommonUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        log.info("POST: Create address with body: {}", CommonUtils.toJsonString(request));
        return addressService.create(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<AddressResponse>> findAll() {
        log.info("GET: All addresses");
        return addressService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<AddressResponse> findById(@PathVariable UUID id) {
        log.info("GET: Address by id: {}", id);
        return addressService.findById(id);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<AddressResponse> update(@PathVariable UUID id, @Valid @RequestBody AddressRequest request) {
        log.info("PUT: Update address with id: {} and body: {}", id, CommonUtils.toJsonString(request));
        return addressService.update(id, request);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Void> delete(@PathVariable UUID id) {
        log.info("DELETE: Address by id: {}", id);
        return addressService.delete(id);
    }

    @PostMapping("/list-by-ids")
    public ResponseModel<List<AddressResponse>> getAddressesByIds(@RequestBody List<UUID> ids) {
        log.info("Controller: Received request to fetch {} addresses", ids.size());
        return addressService.getAddressesByIds(ids);
    }
}
package com.microservices.product_service.domain.outbound.feignclient;

import com.microservices.common_service.domain.ResponseModel;
import com.microservices.product_service.config.FeignMultipartSupportConfig;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "attachment-service", url = "${attachment.base.url}", configuration = FeignMultipartSupportConfig.class)
public interface AttachmentClient {

        @PostMapping(value = "/{folder}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        ResponseModel<List<String>> uploadImage(
                        @PathVariable("folder") String folder,
                        @RequestPart("files") List<MultipartFile> files);

        @DeleteMapping("{folder}/{id}")
        ResponseModel<Void> deleteImage(
                        @PathVariable("folder") String folder,
                        @PathVariable("id") String id);
}

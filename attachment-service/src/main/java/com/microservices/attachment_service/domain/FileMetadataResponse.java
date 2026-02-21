package com.microservices.attachment_service.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadataResponse {

    private String id;
    private String folder;
    private long size;
    private String contentType;
}
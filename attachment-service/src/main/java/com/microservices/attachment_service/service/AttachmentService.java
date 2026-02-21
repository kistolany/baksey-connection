package com.microservices.attachment_service.service;

import com.microservices.attachment_service.domain.FileMetadataResponse;
import com.microservices.common_service.domain.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    ResponseModel<String> upload(String folder, MultipartFile file);

    ResponseModel<Void> delete(String folder, String id);

    ResponseModel<FileMetadataResponse> getMetadata(String folder, String id);

    ResponseModel<List<FileMetadataResponse>> listFiles(String folder);

    ResponseModel<List<String>> listFolders();
}
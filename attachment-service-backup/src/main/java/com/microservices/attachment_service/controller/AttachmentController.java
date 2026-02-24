package com.microservices.attachment_service.controller;

import com.microservices.attachment_service.domain.FileMetadataResponse;
import com.microservices.attachment_service.service.AttachmentService;
import com.microservices.common_service.domain.ResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService service;

    // Upload
    @PostMapping(value = "/{folder}", consumes = "multipart/form-data")
    public ResponseModel<String> upload(@PathVariable String folder, @RequestParam("file") MultipartFile file) {
        log.info("POST: request to upload file to folder: {}", folder);
        return service.upload(folder, file);
    }

    // Delete
    @DeleteMapping("/{folder}/{id}")
    public ResponseModel<Void> delete(@PathVariable String folder, @PathVariable String id) {
        log.info("DELETE: request to delete file: {} from folder: {}", id, folder);
        return service.delete(folder, id);
    }

    // Get metadata
    @GetMapping("/{folder}/{id}")
    public ResponseModel<FileMetadataResponse> getMetadata(@PathVariable String folder, @PathVariable String id) {
        log.info("GET: request for metadata of file: {} in folder: {}", id, folder);
        return service.getMetadata(folder, id);
    }

    // List files in folder
    @GetMapping("/{folder}/files")
    public ResponseModel<List<FileMetadataResponse>> listFiles(@PathVariable String folder) {
        log.info("GET: request to list files in folder: {}", folder);
        return service.listFiles(folder);
    }

    // List all folders
    @GetMapping("/folders")
    public ResponseModel<List<String>> listFolders() {
        log.info("GET: request to list all folders");
        return service.listFolders();
    }
}
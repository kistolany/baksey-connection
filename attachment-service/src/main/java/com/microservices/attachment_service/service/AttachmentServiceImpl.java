package com.microservices.attachment_service.service;

import com.microservices.attachment_service.domain.FileMetadataResponse;
import com.microservices.attachment_service.repository.MinioRepository;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final MinioRepository repository;

    @Value("${attachment.minio.public-base-url}")
    private String minioBaseUrl;

    @Value("${attachment.minio.bucket}")
    private String bucketName;

    @Override
    public ResponseModel<String> upload(String folder, MultipartFile file) {
        log.info("START   : Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
                : "";

        // Validation
        boolean isValidType = file.getContentType() != null &&
                (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"));
        boolean isValidExt = extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png");

        if (!isValidType || !isValidExt) {
            log.warn("FAILED  : Invalid file type for: {}", originalName);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Only .jpg, .jpeg, and .png allowed");
        }

        String id = UUID.randomUUID() + extension;

        try {
            repository.upload(folder, id, file.getInputStream(), file.getSize(), file.getContentType());

            // FIX 3: Construct the URL correctly: BASE_URL / BUCKET / FOLDER / ID
            String baseUrl = minioBaseUrl.endsWith("/") ? minioBaseUrl : minioBaseUrl + "/";
            String fullUrl = String.format("%s%s/%s/%s", baseUrl, bucketName, folder, id);

            log.info("FINISH  : Uploaded file successfully. URL: {}", fullUrl);
            return ResponseModel.success(fullUrl);

        } catch (IOException e) {
            log.error("ERROR   : Failed to process file stream", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "File processing failed");
        }
    }

    @Override
    public ResponseModel<Void> delete(String folder, String id) {
        log.info("START   : Deleting file: {} from folder: {}", id, folder);
        repository.delete(folder, id);
        log.info("FINISH  : Deleted file: {} successfully", id);
        return ResponseModel.success();
    }

    @Override
    public ResponseModel<FileMetadataResponse> getMetadata(String folder, String id) {
        log.info("START   : Getting metadata for file: {} in folder: {}", id, folder);
        var stat = repository.getObjectStat(folder, id);

        FileMetadataResponse response = FileMetadataResponse.builder()
                .id(id)
                .folder(folder)
                .size(stat.size())
                .contentType(stat.contentType())
                .build();

        log.info("FINISH  : Retrieved metadata for file: {}", id);
        return ResponseModel.success(response);
    }

    @Override
    public ResponseModel<List<FileMetadataResponse>> listFiles(String folder) {
        log.info("START   : Listing files in folder: {}", folder);
        List<FileMetadataResponse> files = repository.list(folder);
        log.info("FINISH  : Found {} files in folder: {}", files.size(), folder);
        return ResponseModel.success(files);
    }

    @Override
    public ResponseModel<List<String>> listFolders() {
        log.info("START   : Listing all folders");
        List<String> folders = repository.listFolders();
        log.info("FINISH  : Retrieved {} folders", folders.size());
        return ResponseModel.success(folders);
    }
}
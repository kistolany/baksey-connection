package com.microservices.attachment_service.repository;

import com.microservices.attachment_service.domain.FileMetadataResponse;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.exception.ApiException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MinioRepository {

    private final MinioClient minioClient;

    @Value("${attachment.minio.bucket}")
    private String bucket;

    // =============================
    // Upload
    // =============================
    public void upload(String folder, String id, InputStream stream, long size, String contentType) {

        try {

            ensureBucketExists();

            String objectName = folder + "/" + id;

            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).stream(stream, size, -1).contentType(contentType).build());

        } catch (Exception e) {
            log.error("Upload failed", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Failed to upload file");
        }
    }

    // =============================
    // Delete
    // =============================
    public void delete(String folder, String id) {

        try {

            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(folder + "/" + id).build());

        } catch (Exception e) {
            log.error("Delete failed", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Failed to delete file");
        }
    }

    // =============================
    // Get Metadata
    // =============================
    public StatObjectResponse getObjectStat(String folder, String id) {

        try {

            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(folder + "/" + id).build());

        } catch (Exception e) {
            log.error("Metadata fetch failed", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "File not found");
        }
    }

    // =============================
    // List Files In Folder
    // =============================
    public List<FileMetadataResponse> list(String folder) {

        List<FileMetadataResponse> files = new ArrayList<>();

        try {

            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(folder + "/").recursive(true).build());

            for (Result<Item> result : results) {

                Item item = result.get();

                files.add(FileMetadataResponse.builder().id(item.objectName().replace(folder + "/", "")).folder(folder).size(item.size()).contentType("unknown").build());
            }

            return files;

        } catch (Exception e) {
            log.error("List files failed", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Failed to list files");
        }
    }

    // =============================
    // List Folders
    // =============================
    public List<String> listFolders() {

        Set<String> folders = new HashSet<>();

        try {

            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).recursive(true).build());

            for (Result<Item> result : results) {

                Item item = result.get();

                String objectName = item.objectName();

                if (objectName.contains("/")) {
                    String folder = objectName.substring(0, objectName.indexOf("/"));
                    folders.add(folder);
                }
            }

            return new ArrayList<>(folders);

        } catch (Exception e) {
            log.error("List folders failed", e);
            throw new ApiException(ResponseConstants.ResponseStatus.BAD_REQUEST, "Failed to list folders");
        }
    }

    // =============================
    // Auto Create Bucket
    // =============================
    private void ensureBucketExists() throws Exception {

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        // Set Public Read Policy (Always apply to ensure existing buckets are public)
        String policy = "{\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Action\": \"s3:GetObject\",\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": \"*\",\n" +
                "      \"Resource\": \"arn:aws:s3:::" + bucket + "/*\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Version\": \"2012-10-17\"\n" +
                "}";

        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
    }
}
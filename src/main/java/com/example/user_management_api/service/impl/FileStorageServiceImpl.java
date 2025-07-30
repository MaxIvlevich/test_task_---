package com.example.user_management_api.service.impl;

import com.example.user_management_api.config.MinioConfig;
import com.example.user_management_api.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    private void makeBucket() {
        try {
            String bucketName = minioConfig.getBucketName();
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.info("Bucket '{}' not found. Creating a new one...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created successfully.", bucketName);
            } else {
                log.info("Bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            log.error("Could not initialize MinIO bucket", e);
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File to upload cannot be empty.");
        }
        try (InputStream inputStream = file.getInputStream()) {
            String fileKey = UUID.randomUUID().toString();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileKey)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(args);
            log.info("File '{}' uploaded successfully to bucket '{}'.", fileKey, minioConfig.getBucketName());
            return fileKey;
        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Error uploading file. Please try again later.", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            log.warn("Attempted to delete a file with an empty key.");
            return;
        }
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileKey)
                    .build();
            minioClient.removeObject(args);
            log.info("File '{}' deleted successfully from bucket '{}'.", fileKey, minioConfig.getBucketName());
        } catch (Exception e) {
            log.error("Error deleting file from MinIO with key: {}", fileKey, e);
            throw new RuntimeException("Error deleting file. Please try again later.", e);
        }
    }
}

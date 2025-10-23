package com.example.schoolmoney.storage.adapter.minio;

import com.example.schoolmoney.common.constants.messages.StorageMessages;
import com.example.schoolmoney.storage.adapter.StorageAdapter;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioAdapter implements StorageAdapter {

    private final MinioClient minioClient;

    private final MinioConfig minioConfig;

    private void ensureBucketExists() {
        try {
            log.debug("Checking if bucket {} exists", minioConfig.getBucketName());
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );
            if (!bucketExists) {
                log.warn("Bucket {} not exists - creating...", minioConfig.getBucketName());
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(minioConfig.getBucketName())
                                .build()
                );
                log.debug("Bucket {} created", minioConfig.getBucketName());
            } else {
                log.debug("Bucket {} exists", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            ensureBucketExists();

            String fileUrl = UUID.randomUUID().toString();

            log.debug("Uploading file {} to bucket {}", fileUrl, minioConfig.getBucketName());
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(fileUrl)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            log.debug("File {} uploaded to bucket {}", fileUrl, minioConfig.getBucketName());

            return fileUrl;
        } catch (Exception e) {
            log.error(StorageMessages.FAILED_TO_UPLOAD_FILE, e);
            throw new RuntimeException(StorageMessages.FAILED_TO_UPLOAD_FILE, e);
        }
    }

    @Override
    public InputStreamResource downloadFile(String fileUrl) {
        try {
            return new InputStreamResource(
                    minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(minioConfig.getBucketName())
                                    .object(fileUrl)
                                    .build()
                    )
            );
        } catch (Exception e) {
            log.error(StorageMessages.FAILED_TO_DOWNLOAD_FILE, e);
            throw new RuntimeException(StorageMessages.FAILED_TO_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileUrl)
                            .build()
            );
        } catch (Exception e) {
            log.error(StorageMessages.FAILED_TO_DELETE_FILE, e);
            throw new RuntimeException(StorageMessages.FAILED_TO_DELETE_FILE, e);
        }
    }

}

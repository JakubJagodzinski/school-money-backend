package com.example.schoolmoney.storage.adapter;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageAdapter {

    String uploadFile(MultipartFile file, String bucketName);

    InputStreamResource downloadFile(String fileUrl, String bucketName);

    void deleteFile(String fileUrl, String bucketName);

}

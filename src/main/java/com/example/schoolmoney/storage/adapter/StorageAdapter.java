package com.example.schoolmoney.storage.adapter;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageAdapter {

    String uploadFile(MultipartFile file);

    InputStreamResource downloadFile(String fileUrl);

    void deleteFile(String fileUrl);

}

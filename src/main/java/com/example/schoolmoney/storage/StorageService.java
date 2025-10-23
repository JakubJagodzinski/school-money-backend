package com.example.schoolmoney.storage;

import com.example.schoolmoney.storage.adapter.StorageAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {

    private final StorageAdapter storageAdapter;

    public String uploadFile(MultipartFile file, String bucketName) {
        log.debug("Uploading file {}", file.getOriginalFilename());
        log.debug("File size: {}", file.getSize());
        log.debug("File content type: {}", file.getContentType());

        String fileUrl = storageAdapter.uploadFile(file, bucketName);
        log.debug("File {} uploaded", fileUrl);

        return fileUrl;
    }

    public InputStreamResource downloadFile(String fileUrl, String bucketName) {
        log.debug("Downloading file {}", fileUrl);

        InputStreamResource inputStreamResource = storageAdapter.downloadFile(fileUrl, bucketName);
        log.debug("File {} downloaded", fileUrl);

        return inputStreamResource;
    }

    public void deleteFile(String fileUrl, String bucketName) {
        log.debug("Deleting file {}", fileUrl);
        storageAdapter.deleteFile(fileUrl, bucketName);
        log.debug("File {} deleted", fileUrl);
    }

}

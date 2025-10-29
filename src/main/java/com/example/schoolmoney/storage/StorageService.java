package com.example.schoolmoney.storage;

import com.example.schoolmoney.files.FileCategory;
import com.example.schoolmoney.files.validation.FileTypeValidator;
import com.example.schoolmoney.files.validation.FileValidationRules;
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

    public String uploadFile(MultipartFile file, String bucketName, FileCategory fileCategory) {
        log.debug("Enter uploadFile(filename={}, bucketName={}, fileCategory={})", file.getOriginalFilename(), bucketName, fileCategory);

        FileTypeValidator.validate(
                file,
                FileValidationRules.getAllowedTypes(fileCategory),
                FileValidationRules.getMaxSize(fileCategory)
        );
        log.debug("File validated successfully: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        String fileUrl = storageAdapter.uploadFile(file, bucketName);
        log.info("File uploaded to {}", fileUrl);

        log.debug("Exit uploadFile");
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

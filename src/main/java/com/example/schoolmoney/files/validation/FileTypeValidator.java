package com.example.schoolmoney.files.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
public class FileTypeValidator {

    private FileTypeValidator() {
    }

    private static float bytesToMegabytes(long maxSizeBytes) {
        return (float) maxSizeBytes / 1024 / 1024;
    }

    public static void validate(MultipartFile file, Set<String> allowedContentTypes, long maxSizeBytes) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            log.debug("File is null or empty");
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            log.debug("Unsupported file type: {}", contentType);
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        if (file.getSize() > maxSizeBytes) {
            log.debug("File too large (max {}MB)", bytesToMegabytes(maxSizeBytes));
            throw new IllegalArgumentException("File too large (max " + bytesToMegabytes(maxSizeBytes) + "MB)");
        }
    }

}

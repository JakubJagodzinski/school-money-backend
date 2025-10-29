package com.example.schoolmoney.files.validation;

import com.example.schoolmoney.files.FileCategory;

import java.util.Map;
import java.util.Set;

public class FileValidationRules {

    private static final long MEGABYTE_SIZE_IN_BYTES = 1024 * 1024L;

    private static final Map<FileCategory, Set<String>> ALLOWED_TYPES = Map.of(
            FileCategory.AVATAR_OR_LOGO, Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            ),
            FileCategory.FUND_MEDIA, Set.of(
                    // IMAGES
                    "image/jpeg",
                    "image/png",
                    "image/webp",
                    "image/gif",
                    "image/svg+xml",
                    // VIDEO
                    "video/mp4",
                    "video/quicktime",
                    "video/webm",
                    // DOCUMENTS
                    "application/pdf",
                    "text/plain",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.oasis.opendocument.text",
                    // ARCHIVE
                    "application/zip",
                    "application/x-zip-compressed",
                    "application/x-rar-compressed",
                    // AUDIO
                    "audio/mpeg",
                    "audio/wav"
            )
    );

    private static final Map<FileCategory, Long> MAX_SIZES = Map.of(
            FileCategory.AVATAR_OR_LOGO, 2 * MEGABYTE_SIZE_IN_BYTES,
            FileCategory.FUND_MEDIA, 10 * MEGABYTE_SIZE_IN_BYTES
    );

    private FileValidationRules() {
    }

    public static Set<String> getAllowedTypes(FileCategory category) {
        Set<String> types = ALLOWED_TYPES.get(category);
        if (types == null) {
            throw new IllegalArgumentException("No validation rules defined for file category: " + category);
        }
        return types;
    }

    public static long getMaxSize(FileCategory category) {
        Long size = MAX_SIZES.get(category);
        if (size == null) {
            throw new IllegalArgumentException("No max size defined for file category: " + category);
        }
        return size;
    }

}

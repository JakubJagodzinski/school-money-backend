package com.example.schoolmoney.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
public class FileTypeDetector {

    private FileTypeDetector() {
    }

    public static FileType determineFileType(String contentType) {
        if (contentType == null) return FileType.OTHER;

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException e) {
            log.debug("[Spring.MediaType] Could not parse content type: {} - defaulting to OTHER", contentType);
            return FileType.OTHER;
        }

        String type = mediaType.getType();
        String subtype = mediaType.getSubtype();

        return switch (type) {
            case "image" -> FileType.IMAGE;
            case "video" -> FileType.VIDEO;
            case "audio" -> FileType.AUDIO;
            case "text" -> FileType.DOCUMENT;
            case "application" -> switch (subtype) {
                case "pdf" -> FileType.DOCUMENT;
                case "zip", "x-rar-compressed", "x-zip-compressed" -> FileType.ARCHIVE;
                default -> {
                    log.debug("[FileType] Could not match type: {} - defaulting to OTHER", contentType);
                    yield FileType.OTHER;
                }
            };
            default -> {
                log.debug("[FileType] Could not match type: {} - defaulting to OTHER", contentType);
                yield FileType.OTHER;
            }
        };
    }

}

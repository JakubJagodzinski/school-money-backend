package com.example.schoolmoney.domain.fundmedia.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileWithMetadata {

    private InputStreamResource resource;

    private String filename;

    private MediaType contentType;

}

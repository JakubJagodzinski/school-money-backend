package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.fundmedia.dto.internal.FileWithMetadata;
import com.example.schoolmoney.domain.fundmedia.dto.request.UpdateFundMediaFileMetadataRequestDto;
import com.example.schoolmoney.domain.fundmedia.dto.response.FundMediaResponseDto;
import com.example.schoolmoney.user.Permission;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundMediaController {

    private final FundMediaService fundMediaService;

    @Operation(
            summary = "Upload a media file for a fund"
    )
    @CheckPermission(Permission.FUND_MEDIA_FILE_UPLOAD)
    @PostMapping(
            value = "/funds/{fundId}/media/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FundMediaResponseDto> uploadFundMediaFile(@PathVariable UUID fundId, @RequestParam("mediaFile") MultipartFile mediaFile) {
        FundMediaResponseDto fundMediaResponseDto = fundMediaService.uploadFundMediaFile(fundId, mediaFile);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fundMediaResponseDto);
    }

    @Operation(
            summary = "Get fund all media metadata"
    )
    @CheckPermission(Permission.FUND_MEDIA_METADATA_READ)
    @GetMapping("/funds/{fundId}/media/file/metadata")
    public ResponseEntity<Page<FundMediaResponseDto>> getFundMediaMetadataPage(
            @PathVariable UUID fundId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "uploadedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundMediaResponseDto> fundMediaResponseDtoPage = fundMediaService.getFundMediaMetadataPage(fundId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundMediaResponseDtoPage);
    }

    @Operation(
            summary = "Get fund media file if exists"
    )
    @CheckPermission(Permission.FUND_MEDIA_FILE_READ)
    @GetMapping("/funds/{fundId}/media/{fundMediaId}/file")
    public ResponseEntity<InputStreamResource> getFundMediaFile(@PathVariable UUID fundId, @PathVariable UUID fundMediaId) {
        FileWithMetadata file = fundMediaService.getFundMediaFileWithMetadata(fundId, fundMediaId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(file.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getResource());
    }

    @CheckPermission(Permission.FUND_MEDIA_METADATA_UPDATE)
    @PatchMapping("/funds/{fundId}/media/{fundMediaId}/file/metadata")
    public ResponseEntity<FundMediaResponseDto> updateFundMediaFileMetadata(
            @PathVariable UUID fundId,
            @PathVariable UUID fundMediaId,
            @RequestBody UpdateFundMediaFileMetadataRequestDto updateFundMediaFileMetadataRequestDto
    ) {
        FundMediaResponseDto fundMediaResponseDto = fundMediaService.updateFundMediaFileMetadata(fundId, fundMediaId, updateFundMediaFileMetadataRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fundMediaResponseDto);
    }

    @Operation(
            summary = "Delete fund media file if exists"
    )
    @CheckPermission(Permission.FUND_MEDIA_FILE_DELETE)
    @DeleteMapping("/funds/{fundId}/media/{fundMediaId}/file")
    public ResponseEntity<Void> deleteFundMediaFile(@PathVariable UUID fundId, @PathVariable UUID fundMediaId) {
        fundMediaService.deleteFundMediaFile(fundId, fundMediaId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

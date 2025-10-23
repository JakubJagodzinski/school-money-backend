package com.example.schoolmoney.domain.fundlogo;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.domain.AvatarMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.Permission;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundLogoController {

    private final FundLogoService fundLogoService;

    @Operation(
            summary = "Update fund logo"
    )
    @CheckPermission(Permission.FUND_LOGO_UPDATE)
    @PatchMapping(
            value = "/funds/{fundId}/logo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateFundLogo(@PathVariable UUID fundId, @RequestParam("logoFile") MultipartFile avatarFile) {
        fundLogoService.updateFundLogo(fundId, avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get fund logo image file if set"
    )
    @CheckPermission(Permission.FUND_LOGO_READ)
    @GetMapping("/funds/{fundId}/logo")
    public ResponseEntity<InputStreamResource> getFundLogo(@PathVariable UUID fundId) {
        InputStreamResource fundAvatarResource = fundLogoService.getFundLogo(fundId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(fundAvatarResource);
    }

    @Operation(
            summary = "Delete fund logo"
    )
    @CheckPermission(Permission.FUND_LOGO_DELETE)
    @DeleteMapping("/funds/{fundId}/logo")
    public ResponseEntity<Void> deleteFundLogo(@PathVariable UUID fundId) {
        fundLogoService.deleteFundLogo(fundId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

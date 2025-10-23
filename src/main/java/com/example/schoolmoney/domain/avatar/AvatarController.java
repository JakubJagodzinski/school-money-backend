package com.example.schoolmoney.domain.avatar;

import com.example.schoolmoney.common.constants.messages.AvatarMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
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
public class AvatarController {

    private final AvatarService avatarService;

    @Operation(
            summary = "Update parent avatar"
    )
    @PatchMapping(
            value = "/parents/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateParentAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) {
        avatarService.updateParentAvatar(avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get parent avatar image file if exists"
    )
    @GetMapping("/parents/{parentId}/avatar")
    public ResponseEntity<InputStreamResource> getParentAvatar(@PathVariable UUID parentId) {
        InputStreamResource parentAvatarResource = avatarService.getParentAvatar(parentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(parentAvatarResource);
    }

    @Operation(
            summary = "Delete parent avatar"
    )
    @DeleteMapping("/parents/avatar")
    public ResponseEntity<Void> deleteParentAvatar() {
        avatarService.deleteParentAvatar();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Update child avatar"
    )
    @PatchMapping(
            value = "/children/{childId}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateChildAvatar(@PathVariable UUID childId, @RequestParam("avatarFile") MultipartFile avatarFile) {
        avatarService.updateChildAvatar(childId, avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get child avatar image file if set"
    )
    @GetMapping("/children/{childId}/avatar")
    public ResponseEntity<InputStreamResource> getChildAvatar(@PathVariable UUID childId) {
        InputStreamResource childAvatarResource = avatarService.getChildAvatar(childId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(childAvatarResource);
    }

    @Operation(
            summary = "Delete child avatar"
    )
    @DeleteMapping("/children/{childId}/avatar")
    public ResponseEntity<Void> deleteChildAvatar(@PathVariable UUID childId) {
        avatarService.deleteChildAvatar(childId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Update fund logo"
    )
    @PatchMapping(
            value = "/funds/{fundId}/logo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateFundLogo(@PathVariable UUID fundId, @RequestParam("logoFile") MultipartFile avatarFile) {
        avatarService.updateFundLogo(fundId, avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get fund logo image file if set"
    )
    @GetMapping("/funds/{fundId}/logo")
    public ResponseEntity<InputStreamResource> getFundLogo(@PathVariable UUID fundId) {
        InputStreamResource fundAvatarResource = avatarService.getFundLogo(fundId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(fundAvatarResource);
    }

    @Operation(
            summary = "Delete fund logo"
    )
    @DeleteMapping("/funds/{fundId}/logo")
    public ResponseEntity<Void> deleteFundLogo(@PathVariable UUID fundId) {
        avatarService.deleteFundLogo(fundId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

package com.example.schoolmoney.domain.childavatar;

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
public class ChildAvatarController {

    private final ChildAvatarService avatarService;

    @Operation(
            summary = "Update child avatar"
    )
    @CheckPermission(Permission.CHILD_AVATAR_UPDATE)
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
    @CheckPermission(Permission.CHILD_AVATAR_READ)
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
    @CheckPermission(Permission.CHILD_AVATAR_DELETE)
    @DeleteMapping("/children/{childId}/avatar")
    public ResponseEntity<Void> deleteChildAvatar(@PathVariable UUID childId) {
        avatarService.deleteChildAvatar(childId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

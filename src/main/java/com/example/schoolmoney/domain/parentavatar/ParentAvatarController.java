package com.example.schoolmoney.domain.parentavatar;

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
public class ParentAvatarController {

    private final ParentAvatarService parentAvatarService;

    @Operation(
            summary = "Update parent avatar"
    )
    @CheckPermission(Permission.PARENT_AVATAR_UPDATE)
    @PatchMapping(
            value = "/parents/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateParentAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) {
        parentAvatarService.updateParentAvatar(avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get parent avatar image file if exists"
    )
    @CheckPermission(Permission.PARENT_AVATAR_READ)
    @GetMapping("/parents/{parentId}/avatar")
    public ResponseEntity<InputStreamResource> getParentAvatar(@PathVariable UUID parentId) {
        InputStreamResource parentAvatarResource = parentAvatarService.getParentAvatar(parentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(parentAvatarResource);
    }

    @Operation(
            summary = "Delete parent avatar"
    )
    @CheckPermission(Permission.PARENT_AVATAR_DELETE)
    @DeleteMapping("/parents/avatar")
    public ResponseEntity<Void> deleteParentAvatar() {
        parentAvatarService.deleteParentAvatar();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

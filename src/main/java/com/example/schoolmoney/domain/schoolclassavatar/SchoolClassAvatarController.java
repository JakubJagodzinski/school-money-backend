package com.example.schoolmoney.domain.schoolclassavatar;

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
public class SchoolClassAvatarController {

    private final SchoolClassAvatarService schoolClassAvatarService;

    @Operation(
            summary = "Update school class avatar"
    )
    @CheckPermission(Permission.SCHOOL_CLASS_AVATAR_UPDATE)
    @PatchMapping(
            value = "/school-classes/{schoolClassId}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateSchoolClassAvatar(@PathVariable UUID schoolClassId, @RequestParam("avatarFile") MultipartFile avatarFile) {
        schoolClassAvatarService.updateSchoolClassAvatar(schoolClassId, avatarFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(AvatarMessages.AVATAR_UPDATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Get school class avatar image file if set"
    )
    @CheckPermission(Permission.SCHOOL_CLASS_AVATAR_READ)
    @GetMapping("/school-classes/{schoolClassId}/avatar")
    public ResponseEntity<InputStreamResource> getSchoolClassAvatar(@PathVariable UUID schoolClassId) {
        InputStreamResource schoolClassAvatarSource = schoolClassAvatarService.getSchoolClassAvatar(schoolClassId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(schoolClassAvatarSource);
    }

    @Operation(
            summary = "Delete school class avatar"
    )
    @CheckPermission(Permission.SCHOOL_CLASS_AVATAR_DELETE)
    @DeleteMapping("/school-classes/{schoolClassId}/avatar")
    public ResponseEntity<Void> deleteChildAvatar(@PathVariable UUID schoolClassId) {
        schoolClassAvatarService.deleteSchoolClassAvatar(schoolClassId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

package com.example.schoolmoney.admin;

import com.example.schoolmoney.admin.dto.request.BlockUserRequestDto;
import com.example.schoolmoney.admin.dto.request.UnblockUserRequestDto;
import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.Permission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @CheckPermission(Permission.USER_BLOCK)
    @PostMapping("/block/{userId}")
    public ResponseEntity<MessageResponseDto> blockUser(@PathVariable UUID userId, @Valid @RequestBody BlockUserRequestDto blockUserRequestDto) {
        adminService.blockUser(userId, blockUserRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(UserMessages.ACCOUNT_BLOCKED_SUCCESSFULLY));
    }

    @CheckPermission(Permission.USER_UNBLOCK)
    @PostMapping("/unblock/{userId}")
    public ResponseEntity<MessageResponseDto> unblockUser(@PathVariable UUID userId, @Valid @RequestBody UnblockUserRequestDto unblockUserRequestDto) {
        adminService.unblockUser(userId, unblockUserRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(UserMessages.ACCOUNT_UNBLOCKED_SUCCESSFULLY));
    }

}

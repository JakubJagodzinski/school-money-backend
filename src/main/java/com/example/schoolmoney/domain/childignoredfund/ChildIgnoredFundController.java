package com.example.schoolmoney.domain.childignoredfund;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ChildIgnoredFundController {

    private final ChildIgnoredFundService childIgnoredFundService;

    @CheckPermission(Permission.CHILD_FUND_IGNORE)
    @PostMapping("/children/{childId}/ignored-funds")
    public ResponseEntity<MessageResponseDto> ignoreFundForChild(@PathVariable UUID childId, @RequestParam UUID fundId) {
        childIgnoredFundService.ignoreFundForChild(childId, fundId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponseDto(FundMessages.FUND_IGNORED_BY_CHILD_SUCCESSFULLY));
    }

    @CheckPermission(Permission.CHILD_FUND_UNIGNORE)
    @DeleteMapping("/children/{childId}/ignored-funds/{fundId}")
    public ResponseEntity<MessageResponseDto> unignoreFundForChild(@PathVariable UUID childId, @PathVariable UUID fundId) {
        childIgnoredFundService.unignoreFundForChild(childId, fundId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(FundMessages.FUND_UNIGNORED_BY_CHILD_SUCCESSFULLY));
    }

}

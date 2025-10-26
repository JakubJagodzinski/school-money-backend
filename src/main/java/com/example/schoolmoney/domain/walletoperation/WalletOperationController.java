package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.walletoperation.dto.response.WalletOperationResponseDto;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletOperationController {

    private final WalletOperationService walletOperationService;


    @CheckPermission(Permission.WALLET_HISTORY_READ_ALL)
    @GetMapping("/wallet/history")
    public ResponseEntity<Page<WalletOperationResponseDto>> getWalletHistory(
            @ParameterObject
            @PageableDefault(size = 20, sort = "processedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<WalletOperationResponseDto> walletOperationPage = walletOperationService.getWalletHistory(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletOperationPage);
    }

}

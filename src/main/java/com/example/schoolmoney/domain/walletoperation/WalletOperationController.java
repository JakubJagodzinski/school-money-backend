package com.example.schoolmoney.domain.walletoperation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletOperationController {

    private final WalletOperationService walletOperationService;

}

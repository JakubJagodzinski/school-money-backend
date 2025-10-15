package com.example.schoolmoney.domain.walletoperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletOperationService {

    private final WalletOperationRepository walletOperationRepository;

}

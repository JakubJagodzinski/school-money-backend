package com.example.schoolmoney.domain.fundoperation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundOperationController {

    private final FundOperationService fundOperationService;

}

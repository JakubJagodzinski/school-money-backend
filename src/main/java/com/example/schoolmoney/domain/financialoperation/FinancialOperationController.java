package com.example.schoolmoney.domain.financialoperation;

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
public class FinancialOperationController {

    private final FinancialOperationService financialOperationService;

    @GetMapping("/financial-operations/history")
    public ResponseEntity<Page<FinancialOperationView>> getUserFinancialOperationHistory(
            @ParameterObject
            @PageableDefault(size = 20, sort = "processedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FinancialOperationView> financialOperationViewPage = financialOperationService.getUserFinancialOperationHistory(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(financialOperationViewPage);
    }

}

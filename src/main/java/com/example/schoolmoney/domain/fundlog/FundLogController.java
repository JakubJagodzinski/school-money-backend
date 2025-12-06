package com.example.schoolmoney.domain.fundlog;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundLogController {

    private final FundLogService fundLogService;

    @GetMapping("/funds/{fundId}/logs")
    public ResponseEntity<Page<FundLogView>> getFundLogs(
            @PathVariable UUID fundId,
            @ParameterObject @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundLogView> responseDto = fundLogService.getFundLogs(fundId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

}

package com.example.schoolmoney.domain.fundlog;

import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundlog.dto.response.FundLogResponseDto;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundLogController {

    private final FundLogService fundLogService;

    @GetMapping("/funds/{fundId}/logs")
    public ResponseEntity<Page<FundLogResponseDto>> getFundLogs(
            @PathVariable UUID fundId,
            @ParameterObject @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundLogResponseDto> responseDto = fundLogService.getFundLogs(fundId, null, null, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @GetMapping("/school-classes/{schoolClassId}/funds/logs")
    public ResponseEntity<Page<FundLogResponseDto>> getFundLogs(
            @PathVariable UUID schoolClassId,
            @RequestParam(required = false) FundStatus fundStatus,
            @ParameterObject @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FundLogResponseDto> responseDto = fundLogService.getFundLogs(null, schoolClassId, fundStatus, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

}

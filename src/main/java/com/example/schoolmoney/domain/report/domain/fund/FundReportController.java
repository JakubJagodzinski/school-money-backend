package com.example.schoolmoney.domain.report.domain.fund;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FundReportController {

    private final FundReportService fundReportService;

    @CheckPermission(Permission.FUND_REPORT_GENERATE)
    @GetMapping("/funds/{fundId}/report")
    public ResponseEntity<byte[]> generateFundReport(@PathVariable UUID fundId) {
        ReportDto reportDto = fundReportService.generateFundReport(fundId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(reportDto.getReportFileName())
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(reportDto.getReport());
    }

}

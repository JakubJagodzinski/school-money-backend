package com.example.schoolmoney.domain.fundreport;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.domain.fundreport.dto.FundReportDto;
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
    @GetMapping("/fund/{fundId}/report")
    public ResponseEntity<byte[]> generateFundReport(@PathVariable UUID fundId) {
        FundReportDto fundReportDto = fundReportService.generateFundReport(fundId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fundReportDto.getFundReportFileName())
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fundReportDto.getFundReport());
    }

}

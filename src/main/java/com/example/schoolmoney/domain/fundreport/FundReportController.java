package com.example.schoolmoney.domain.fundreport;

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

    @GetMapping("/fund/{fundId}/report")
    public ResponseEntity<byte[]> generateFundReport(@PathVariable UUID fundId) {
        byte[] fundReportPdf = fundReportService.generateFundReport(fundId);
        String reportFilename = fundReportService.generateReportFilename(fundId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(reportFilename)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fundReportPdf);
    }

}

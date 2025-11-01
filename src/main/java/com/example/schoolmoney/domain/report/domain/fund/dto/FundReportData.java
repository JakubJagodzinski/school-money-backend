package com.example.schoolmoney.domain.report.domain.fund.dto;

import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.report.dto.ReportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundReportData implements ReportData {

    private Fund fund;

    private InputStreamResource fundLogo;

    private List<FundOperation> fundOperationList;

    private List<FundChildStatusResponseDto> fundChildrenStatuses;

    private List<FundMediaOperation> fundMediaOperations;

}

package com.example.schoolmoney.domain.report.domain.child.dto;

import com.example.schoolmoney.domain.child.Child;
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
public class ChildReportData implements ReportData {

    private Child child;

    private InputStreamResource childAvatar;

    private List<FundOperation> childFundOperationList;

    private long childTotalParticipatedFunds;

}

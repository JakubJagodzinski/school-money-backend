package com.example.schoolmoney.domain.report.domain.schoolclass.dto;

import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassReportData implements ReportData {

    private SchoolClass schoolClass;

    private InputStreamResource schoolClassAvatar;

    private Map<Fund, List<FundChildStatusResponseDto>> schoolClassFundsWithChildrenStatuses;

    private List<ChildWithParentInfoResponseDto> schoolClassChildrenWithParentInfo;

}

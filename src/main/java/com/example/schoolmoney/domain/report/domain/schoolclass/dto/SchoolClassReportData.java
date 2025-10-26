package com.example.schoolmoney.domain.report.domain.schoolclass.dto;

import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassReportData implements ReportData {

    private SchoolClass schoolClass;

    private InputStreamResource schoolClassAvatar;

    private long schoolClassTotalFunds;

    private long schoolClassTotalChildren;

}

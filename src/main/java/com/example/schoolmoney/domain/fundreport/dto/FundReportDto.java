package com.example.schoolmoney.domain.fundreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundReportDto {

    byte[] fundReport;

    String fundReportFileName;

}

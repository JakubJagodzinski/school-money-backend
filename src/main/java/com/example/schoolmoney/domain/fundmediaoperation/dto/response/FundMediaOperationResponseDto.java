package com.example.schoolmoney.domain.fundmediaoperation.dto.response;

import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationType;
import com.example.schoolmoney.files.FileType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
        "operation_id",
        "performed_by_id",
        "performed_by_full_name",
        "fund_media_id",
        "filename",
        "file_type",
        "fund_id",
        "operation_type",
        "processed_at"
})
public class FundMediaOperationResponseDto {

    @JsonProperty("operation_id")
    private UUID operationId;

    @JsonProperty("performed_by_id")
    private UUID performedById;

    @JsonProperty("performed_by_full_name")
    private String performedByFullName;

    @JsonProperty("fund_media_id")
    private UUID fundMediaId;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("file_type")
    private FileType fileType;

    @JsonProperty("fund_id")
    private UUID fundId;

    @JsonProperty("operation_type")
    private FundMediaOperationType operationType;

    @JsonProperty("processed_at")
    private Instant processedAt;

}

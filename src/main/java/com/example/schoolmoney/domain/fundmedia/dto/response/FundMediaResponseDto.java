package com.example.schoolmoney.domain.fundmedia.dto.response;

import com.example.schoolmoney.domain.fundmedia.FundMediaType;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "fund_media_id",
        "fund_id",
        "file_size",
        "content_type",
        "media_type",
        "filename",
        "uploaded_by",
        "uploaded_at",
        "updated_at"
})
public class FundMediaResponseDto {

    @JsonProperty("fund_media_id")
    private UUID fundMediaId;

    @JsonProperty("fund_id")
    private UUID fundId;

    @JsonProperty("file_size")
    private long fileSize;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("media_type")
    private FundMediaType mediaType;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("uploaded_by")
    private ParentResponseDto uploadedBy;

    @JsonProperty("uploaded_at")
    private Instant uploadedAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

}

package com.example.schoolmoney.domain.fundmedia.dto.request;

import com.example.schoolmoney.domain.fundmedia.FundMediaType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "media_type",
        "filename"
})
public class UpdateFundMediaFileMetadataRequestDto {

    @JsonProperty("media_type")
    @Enumerated(EnumType.STRING)
    private FundMediaType mediaType;

    @JsonProperty("filename")
    private String filename;

}

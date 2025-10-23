package com.example.schoolmoney.domain.fundmedia.dto;

import com.example.schoolmoney.domain.fundmedia.FundMedia;
import com.example.schoolmoney.domain.fundmedia.dto.request.UpdateFundMediaFileMetadataRequestDto;
import com.example.schoolmoney.domain.fundmedia.dto.response.FundMediaResponseDto;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ParentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FundMediaMapper {

    @Named("fundMediaToFundMediaDto")
    @Mapping(target = "uploadedBy", source = "uploadedBy", qualifiedByName = "parentToParentDto")
    @Mapping(target = "fundId", source = "fund.fundId")
    FundMediaResponseDto toDto(FundMedia entity);

    void updateEntityFromDto(UpdateFundMediaFileMetadataRequestDto dto, @MappingTarget FundMedia entity);

}

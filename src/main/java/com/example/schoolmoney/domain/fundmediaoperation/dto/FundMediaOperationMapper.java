package com.example.schoolmoney.domain.fundmediaoperation.dto;

import com.example.schoolmoney.domain.fundmedia.dto.FundMediaMapper;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperation;
import com.example.schoolmoney.domain.fundmediaoperation.dto.response.FundMediaOperationResponseDto;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ParentMapper.class, FundMediaMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FundMediaOperationMapper {

    @Named("fundMediaOperationToFundMediaOperationDto")
    FundMediaOperationResponseDto toDto(FundMediaOperation entity);

}

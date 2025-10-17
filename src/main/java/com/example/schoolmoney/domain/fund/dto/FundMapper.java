package com.example.schoolmoney.domain.fund.dto;

import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FundMapper {

    @Mapping(target = "authorId", source = "author.userId")
    @Mapping(target = "schoolClassId", source = "schoolClass.schoolClassId")
    FundResponseDto toDto(Fund entity);

}

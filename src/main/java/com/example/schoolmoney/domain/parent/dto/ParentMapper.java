package com.example.schoolmoney.domain.parent.dto;

import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ParentMapper {

    ParentResponseDto toDto(Parent entity);

}
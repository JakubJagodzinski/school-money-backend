package com.example.schoolmoney.domain.schoolclass.dto;

import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SchoolClassMapper {

    @Mapping(target = "treasurerId", source = "treasurer.userId")
    SchoolClassResponseDto toDto(SchoolClass entity);

}

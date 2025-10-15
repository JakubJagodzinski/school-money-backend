package com.example.schoolmoney.domain.child.dto;

import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChildMapper {

    ChildResponseDto toDto(Child entity);

    Child toEntity(CreateChildRequestDto dto);

    void updateEntityFromDto(CreateChildRequestDto dto, @MappingTarget Child entity);

}

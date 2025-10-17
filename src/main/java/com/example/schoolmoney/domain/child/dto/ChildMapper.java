package com.example.schoolmoney.domain.child.dto;

import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChildMapper {

    @Mapping(target = "parentId", source = "parent.userId")
    @Mapping(target = "schoolClassId", source = "schoolClass.schoolClassId")
    ChildResponseDto toDto(Child entity);

}

package com.example.schoolmoney.domain.child.dto;

import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ParentMapper.class, SchoolClassMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChildMapper {

    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToParentPublicDto")
    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    ChildResponseDto toDto(Child entity);

    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    ChildShortInfoResponseDto toShortInfoDto(Child entity);

}

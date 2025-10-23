package com.example.schoolmoney.domain.child.dto;

import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.dto.request.UpdateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildWithSchoolClassInfoResponseDto;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ParentMapper.class, SchoolClassMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChildMapper {

    @Named("childToChildDto")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToParentDto")
    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    ChildResponseDto toDto(Child entity);

    @Named("childToChildShortInfoDto")
    ChildShortInfoResponseDto toShortInfoDto(Child entity);

    @Named("childToChildWithSchoolClassInfoDto")
    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    ChildWithSchoolClassInfoResponseDto toWithSchoolClassInfoDto(Child entity);

    @Named("childToChildWithParentInfoDto")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToParentDto")
    ChildWithParentInfoResponseDto toWithParentInfoDto(Child entity);

    void updateEntityFromDto(UpdateChildRequestDto dto, @MappingTarget Child entity);

}

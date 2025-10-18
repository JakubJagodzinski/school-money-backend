package com.example.schoolmoney.domain.schoolclass.dto;

import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassHeaderResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ParentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SchoolClassMapper {

    @Mapping(target = "treasurer", source = "treasurer", qualifiedByName = "parentToParentPublicDto")
    SchoolClassResponseDto toDto(SchoolClass entity);

    @Named("schoolClassToSchoolClassHeaderDto")
    SchoolClassHeaderResponseDto toHeaderDto(SchoolClass entity);

}

package com.example.schoolmoney.domain.parent.dto;

import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.dto.request.UpdateParentRequestDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentPublicResponseDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ParentMapper {

    @Named("parentToParentDto")
    ParentResponseDto toDto(Parent entity);

    @Named("parentToParentPublicDto")
    ParentPublicResponseDto toPublicDto(Parent entity);

    void updateEntityFromDto(UpdateParentRequestDto dto, @MappingTarget Parent entity);

}

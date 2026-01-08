package com.example.schoolmoney.domain.fund.dto;

import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.request.UpdateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import com.example.schoolmoney.domain.fund.dto.response.FundWithChildrenResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {SchoolClassMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FundMapper {

    @Named("fundToFundDto")
    @Mapping(target = "authorId", source = "author.userId")
    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    FundResponseDto toDto(Fund entity);

    @Named("fundToFundDtoWithChildren")
    @Mapping(target = "authorId", source = "author.userId")
    @Mapping(target = "schoolClass", source = "schoolClass", qualifiedByName = "schoolClassToSchoolClassHeaderDto")
    FundWithChildrenResponseDto toDtoWithChildren(Fund entity);

    void updateEntityFromDto(UpdateFundRequestDto dto, @MappingTarget Fund entity);

}

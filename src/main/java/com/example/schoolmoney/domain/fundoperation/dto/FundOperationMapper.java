package com.example.schoolmoney.domain.fundoperation.dto;

import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.dto.response.FundOperationResponseDto;
import com.example.schoolmoney.domain.parent.dto.ParentMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                ParentMapper.class,
                ChildMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FundOperationMapper {

    @Named("fundOperationToFundOperationDto")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "parentToParentDto")
    @Mapping(target = "child", source = "child", qualifiedByName = "childToChildShortInfoDto")
    @Mapping(target = "fundId", source = "fund.fundId")
    FundOperationResponseDto toDto(FundOperation entity);

}

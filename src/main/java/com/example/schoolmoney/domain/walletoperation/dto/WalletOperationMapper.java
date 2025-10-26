package com.example.schoolmoney.domain.walletoperation.dto;

import com.example.schoolmoney.domain.walletoperation.WalletOperation;
import com.example.schoolmoney.domain.walletoperation.dto.response.WalletOperationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletOperationMapper {

    @Named("walletOperationToWalletOperationDto")
    WalletOperationResponseDto toDto(WalletOperation entity);

}

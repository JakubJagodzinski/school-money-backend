package com.example.schoolmoney.domain.wallet.dto;

import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.dto.response.WalletResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletMapper {

    @Named("walletToWalletDto")
    WalletResponseDto toDto(Wallet entity);

}

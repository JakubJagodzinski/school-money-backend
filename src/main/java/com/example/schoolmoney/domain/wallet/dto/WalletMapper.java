package com.example.schoolmoney.domain.wallet.dto;

import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletMapper {

    @Named("walletToWalletInfoDto")
    WalletInfoResponseDto toInfoDto(Wallet entity);

    @Named("walletToWalletBalanceDto")
    WalletBalanceResponseDto toBalanceDto(Wallet entity);

}

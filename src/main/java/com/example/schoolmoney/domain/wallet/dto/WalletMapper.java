package com.example.schoolmoney.domain.wallet.dto;

import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WalletMapper {

    @Named("walletToWalletInfoDto")
    @Mapping(target = "balanceInCents", expression = "java(entity.getAvailableBalanceInCents())")
    WalletInfoResponseDto toInfoDto(Wallet entity);

    @Named("walletToWalletBalanceDto")
    @Mapping(target = "balanceInCents", expression = "java(entity.getAvailableBalanceInCents())")
    WalletBalanceResponseDto toBalanceDto(Wallet entity);

}

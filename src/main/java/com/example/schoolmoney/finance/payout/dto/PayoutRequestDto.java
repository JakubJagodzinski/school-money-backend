package com.example.schoolmoney.finance.payout.dto;

import com.example.schoolmoney.finance.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequestDto {

    private String payoutName;

    private ProviderType providerType;

    private UUID userId;

    private UUID operationId;

    private long amountInCents;

    private String currency;

    private String iban;

}

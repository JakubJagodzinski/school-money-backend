package com.example.schoolmoney.finance.payment.dto;

import com.example.schoolmoney.finance.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private ProviderType providerType;

    private String paymentName;

    private UUID operationId;

    private UUID userId;

    private long amountInCents;

    private Currency currency;

    private String successUrl;

    private String cancelUrl;

}

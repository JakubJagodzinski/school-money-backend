package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.finance.payment.ProviderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_operations")
public class WalletOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_operation_id")
    private UUID walletOperationId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_wallet_operations_wallet_id"))
    private Wallet wallet;

    @NotNull
    @Column(name = "external_operation_id", updatable = false)
    private String externalOperationId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, updatable = false)
    private ProviderType providerType;

    @Size(max = 34)
    @Column(name = "iban", updatable = false, length = 34)
    private String iban;

    @NotNull
    @Min(1)
    @Column(name = "amount_in_cents", nullable = false, updatable = false)
    private long amountInCents;

    @NotNull
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private WalletOperationType operationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false, updatable = false)
    private FinancialOperationStatus operationStatus;

    @PrePersist
    protected void onCreate() {
        this.processedAt = Instant.now();
    }

}

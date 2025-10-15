package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.domain.wallet.Wallet;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @Column(name = "stripe_payment_id", nullable = false, updatable = false)
    private UUID stripePaymentId;

    @NotNull
    @Min(1)
    @Column(name = "amount_in_cents", nullable = false, updatable = false)
    private Long amountInCents;

    @NotNull
    @Builder.Default
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private WalletOperationType operationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false, updatable = false)
    private WalletOperationStatus operationStatus;

}

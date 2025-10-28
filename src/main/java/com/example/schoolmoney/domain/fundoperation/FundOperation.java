package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.converter.CurrencyAttributeConverter;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.wallet.Wallet;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fund_operations")
public class FundOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fund_operation_id")
    private UUID fundOperationId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_operations_parent_id"))
    private Parent parent;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_operations_child_id"))
    private Child child;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_operations_fund_id"))
    private Fund fund;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_fund_operations_wallet_id"))
    private Wallet wallet;

    @NotNull
    @Min(0)
    @Column(name = "amount_in_cents", nullable = false, updatable = false)
    private long amountInCents;

    @NotNull
    @Convert(converter = CurrencyAttributeConverter.class)
    @Column(name = "currency", nullable = false, updatable = false)
    private Currency currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private FundOperationType operationType;

    @NotNull
    @Builder.Default
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false, updatable = false)
    private FinancialOperationStatus operationStatus;

}

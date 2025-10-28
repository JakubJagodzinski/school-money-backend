package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.converter.CurrencyAttributeConverter;
import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Currency;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id")
    private UUID walletId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_wallets_parent_id"))
    private Parent parent;

    @NotNull
    @Min(0)
    @Column(name = "balance_in_cents", nullable = false)
    private long balanceInCents;

    @NotNull
    @Convert(converter = CurrencyAttributeConverter.class)
    @Column(name = "currency", nullable = false, updatable = false)
    private Currency currency;

    @NotNull
    @Min(0)
    @Column(name = "reserved_balance_in_cents", nullable = false)
    private long reservedBalanceInCents;

    @Size(max = 34)
    @Column(name = "withdrawal_iban", length = 34)
    private String withdrawalIban;

    public long getAvailableBalanceInCents() {
        return balanceInCents - reservedBalanceInCents;
    }

    public void increaseBalanceInCents(long amountInCents) {
        balanceInCents += amountInCents;
    }

    public void decreaseBalanceInCents(long amountInCents) {
        balanceInCents -= amountInCents;
    }

    public void increaseReservedBalanceInCents(long amountInCents) {
        reservedBalanceInCents += amountInCents;
    }

    public void decreaseReservedBalanceInCents(long amountInCents) {
        reservedBalanceInCents -= amountInCents;
    }

}

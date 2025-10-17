package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @Size(max = 34)
    @Column(name = "withdrawal_iban", length = 34)
    private String withdrawalIban;

}

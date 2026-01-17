package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.converter.CurrencyAttributeConverter;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.utils.IbanUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "funds")
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fund_id")
    private UUID fundId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_funds_author_id"))
    private Parent author;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "school_class_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_funds_school_class_id"))
    private SchoolClass schoolClass;

    @NotBlank
    @Size(min = 3, max = 80)
    @Column(name = "title", nullable = false, length = 80)
    private String title;

    @Column(name = "logo_id")
    private UUID logoId;

    @Size(max = 1_000)
    @Column(name = "description", length = 1_000)
    private String description;

    @NotNull
    @Min(0) // allow "free" funds
    @Column(name = "amount_per_child_in_cents", nullable = false, updatable = false)
    private long amountPerChildInCents;

    @NotNull
    @Convert(converter = CurrencyAttributeConverter.class)
    @Column(name = "currency", nullable = false, updatable = false)
    private Currency currency;

    @NotBlank
    @Size(max = 34)
    @Column(name = "iban", nullable = false, updatable = false, length = 34)
    private String iban;

    @NotNull
    @Column(name = "starts_at", nullable = false, updatable = false)
    private Instant startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fund_status", nullable = false)
    private FundStatus fundStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (startsAt == null) {
            this.startsAt = now;
        }

        if (now.isBefore(startsAt)) {
            fundStatus = FundStatus.SCHEDULED;
        } else {
            fundStatus = FundStatus.ACTIVE;
        }

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return fundStatus.equals(FundStatus.ACTIVE);
    }

    public boolean isFinished() {
        return fundStatus.equals(FundStatus.FINISHED);
    }

    public boolean isCancelled() {
        return fundStatus.equals(FundStatus.CANCELLED);
    }

    public boolean isBlocked() {
        return fundStatus.equals(FundStatus.BLOCKED);
    }

    public void finish() {
        fundStatus = FundStatus.FINISHED;
        endedAt = Instant.now();
    }

    public void cancel() {
        fundStatus = FundStatus.CANCELLED;
        endedAt = Instant.now();
    }

    public String getMaskedIban() {
        return IbanUtil.maskIban(iban);
    }

}

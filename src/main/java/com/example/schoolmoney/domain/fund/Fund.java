package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.converter.CurrencyAttributeConverter;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Column(name = "starts_at", nullable = false, updatable = false)
    private Instant startsAt;

    @NotNull
    @Future
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

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
    @Column(name = "iban", nullable = false, length = 34)
    private String iban;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "fund_status", nullable = false)
    private FundStatus fundStatus = FundStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.startsAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

}

package com.example.schoolmoney.domain.childignoredfund;

import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.fund.Fund;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "child_ignored_funds")
public class ChildIgnoredFund {

    @EmbeddedId
    private ChildIgnoredFundId id;

    @ManyToOne
    @MapsId("childId")
    @JoinColumn(name = "child_id", foreignKey = @ForeignKey(name = "fk_child_ignored_funds_child_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Child child;

    @ManyToOne
    @MapsId("fundId")
    @JoinColumn(name = "fund_id", foreignKey = @ForeignKey(name = "fk_child_ignored_funds_fund_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Fund fund;

    @NotNull
    @Builder.Default
    @Column(name = "ignored_at", nullable = false, updatable = false)
    private Instant ignoredAt = Instant.now();

}

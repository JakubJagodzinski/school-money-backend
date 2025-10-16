package com.example.schoolmoney.domain.childignoredfund;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChildIgnoredFundId {

    @Column(name = "child_id")
    private UUID childId;

    @Column(name = "fund_id")
    private UUID fundId;

}

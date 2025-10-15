package com.example.schoolmoney.domain.childignoredfund;

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

    private UUID childId;

    private UUID fundId;

}

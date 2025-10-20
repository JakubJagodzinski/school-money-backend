package com.example.schoolmoney.domain.financialoperation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT 1 AS id")
public class FinancialOperation {

    @Id
    private Long id;

}

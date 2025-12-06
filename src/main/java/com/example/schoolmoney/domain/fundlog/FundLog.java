package com.example.schoolmoney.domain.fundlog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT 1 AS id")
public class FundLog {

    @Id
    private Long id;

}

package com.example.schoolmoney.domain.childfund;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT 1 AS id")
public class ChildFund {

    @Id
    private Long id;

}

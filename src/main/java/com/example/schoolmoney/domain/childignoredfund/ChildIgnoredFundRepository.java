package com.example.schoolmoney.domain.childignoredfund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildIgnoredFundRepository extends JpaRepository<ChildIgnoredFund, ChildIgnoredFundId> {
}

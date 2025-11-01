package com.example.schoolmoney.domain.childignoredfund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChildIgnoredFundRepository extends JpaRepository<ChildIgnoredFund, ChildIgnoredFundId> {

    int deleteByChild_ChildIdAndFund_FundId(UUID childId, UUID fundId);

    List<ChildIgnoredFund> findAllByFund_FundId(UUID fundId);

}

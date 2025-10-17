package com.example.schoolmoney.domain.fundoperation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundOperationRepository extends JpaRepository<FundOperation, UUID> {

    boolean existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndFundOperationTypeAndFundOperationStatus(
            UUID fundId, UUID userId, UUID childId, FundOperationType fundOperationType, FundOperationStatus fundOperationStatus
    );

    List<FundOperation> findAllByFund_FundId(UUID fundId);

}

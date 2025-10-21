package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundOperationRepository extends JpaRepository<FundOperation, UUID> {

    boolean existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
            UUID fundId, UUID userId, UUID childId, FundOperationType operationType, FinancialOperationStatus operationStatus
    );

    List<FundOperation> findAllByFund_FundId(UUID fundId);

    List<FundOperation> findAllByFund_FundIdOrderByProcessedAtAsc(UUID fundId);

    long countDistinctChild_ChildIdByFund_FundId(UUID fundId);

}

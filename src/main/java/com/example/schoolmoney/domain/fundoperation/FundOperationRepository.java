package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.FundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FundOperationRepository extends JpaRepository<FundOperation, UUID> {

    boolean existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
            UUID fundId,
            UUID userId,
            UUID childId,
            FundOperationType operationType,
            FinancialOperationStatus operationStatus
    );

    boolean existsByChild_ChildIdAndFund_FundStatusAndOperationStatus(
            UUID childId,
            FundStatus fundStatus,
            FinancialOperationStatus operationStatus
    );

    boolean existsByFund_FundIdAndParent_UserId(UUID fundId, UUID userId);

    List<FundOperation> findAllByFund_FundId(UUID fundId);

    List<FundOperation> findAllByFund_FundIdAndChild_Parent_UserId(UUID fundId, UUID userId);

    Page<FundOperation> findAllByFund_FundIdOrderByProcessedAtDesc(UUID fundId, Pageable pageable);

    List<FundOperation> findAllByFund_FundIdOrderByProcessedAtAsc(UUID fundId);

    List<FundOperation> findAllByChild_ChildIdOrderByProcessedAtAsc(UUID childId);

    @Query("""
                SELECT DISTINCT fo.child.childId
                FROM FundOperation fo
                WHERE fo.fund.fundId = :fundId
                  AND fo.child IS NOT NULL
                  AND fo.operationStatus = :operationStatusSuccess
            """)
    Set<UUID> findFundAllPaidChildrenIds(
            @Param("fundId") UUID fundId,
            @Param("operationStatusSuccess") FinancialOperationStatus operationStatusSuccess
    );

    @Query("""
                SELECT DISTINCT fo.child.childId
                FROM FundOperation fo
                WHERE fo.fund.fundId = :fundId
                  AND fo.child IS NOT NULL
                  AND fo.child.parent.userId = :parentId
                  AND fo.operationStatus = :operationStatusSuccess
            """)
    Set<UUID> findFundParentPaidChildrenIds(
            @Param("fundId") UUID fundId,
            @Param("parentId") UUID parentId,
            @Param("operationStatusSuccess") FinancialOperationStatus operationStatusSuccess
    );

    @Query("""
            SELECT COUNT(DISTINCT f.fund.fundId)
            FROM FundOperation f
            WHERE f.child.childId = :childId
            """)
    long countDistinctFundsByChildId(
            @Param("childId") UUID childId
    );

    @Query("""
                SELECT COALESCE(SUM(
                    CASE
                        WHEN fo.operationType = :payment THEN fo.amountInCents
                        WHEN fo.operationType = :deposit THEN fo.amountInCents
                        WHEN fo.operationType = :refund THEN -fo.amountInCents
                        WHEN fo.operationType = :withdrawal THEN -fo.amountInCents
                    END
                ), 0)
                FROM FundOperation fo
                WHERE fo.fund.schoolClass.schoolClassId = :schoolClassId
                  AND fo.fund.fundStatus = :fundStatus
                  AND fo.operationStatus = :operationStatusSuccess
            """)
    long getSchoolClassFundsCurrentBalanceInCents(
            @Param("schoolClassId") UUID schoolClassId,
            @Param("fundStatus") FundStatus fundStatus,
            @Param("payment") FundOperationType payment,
            @Param("deposit") FundOperationType deposit,
            @Param("refund") FundOperationType refund,
            @Param("withdrawal") FundOperationType withdrawal,
            @Param("operationStatusSuccess") FinancialOperationStatus operationStatusSuccess
    );

    @Query("""
                SELECT COALESCE(SUM(
                    CASE
                        WHEN fo.operationType = :payment THEN fo.amountInCents
                        WHEN fo.operationType = :deposit THEN fo.amountInCents
                        WHEN fo.operationType = :refund THEN -fo.amountInCents
                        WHEN fo.operationType = :withdrawal THEN -fo.amountInCents
                    END
                ), 0)
                FROM FundOperation fo
                WHERE fo.fund.fundId = :fundId
                    AND fo.operationStatus = :operationStatusSuccess
            """)
    long getFundCurrentBalanceInCents(
            @Param("fundId") UUID fundId,
            @Param("payment") FundOperationType payment,
            @Param("deposit") FundOperationType deposit,
            @Param("refund") FundOperationType refund,
            @Param("withdrawal") FundOperationType withdrawal,
            @Param("operationStatusSuccess") FinancialOperationStatus operationStatusSuccess
    );

    @Query("""
                SELECT COALESCE(SUM(
                    CASE
                        WHEN fo.operationType = :fundDepositType
                            THEN -fo.amountInCents
                        WHEN fo.operationType = :fundWithdrawalType
                            THEN fo.amountInCents
                        ELSE 0
                    END
                ), 0)
                FROM FundOperation fo
                WHERE fo.fund.fundId = :fundId
                  AND fo.operationStatus = :fundOperationStatusSuccess
            """)
    long getRemainingDepositLimitInCents(
            @Param("fundId") UUID fundId,
            @Param("fundDepositType") FundOperationType fundDepositType,
            @Param("fundWithdrawalType") FundOperationType fundWithdrawalType,
            @Param("fundOperationStatusSuccess") FinancialOperationStatus fundOperationStatusSuccess
    );

}

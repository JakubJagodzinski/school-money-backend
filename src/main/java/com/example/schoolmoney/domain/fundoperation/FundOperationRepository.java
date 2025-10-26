package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundOperationRepository extends JpaRepository<FundOperation, UUID> {

    boolean existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
            UUID fundId, UUID userId, UUID childId, FundOperationType operationType, FinancialOperationStatus operationStatus
    );

    List<FundOperation> findAllByFund_FundId(UUID fundId);

    Page<FundOperation> findAllByFund_FundIdOrderByProcessedAtDesc(UUID fundId, Pageable pageable);

    List<FundOperation> findAllByFund_FundIdOrderByProcessedAtAsc(UUID fundId);

    List<FundOperation> findAllByChild_ChildIdOrderByProcessedAtAsc(UUID childId);

    @Query("""
            SELECT COUNT(DISTINCT f.child.childId)
            FROM FundOperation f
            WHERE f.fund.fundId = :fundId
            """)
    long countDistinctChildrenByFundId(@Param("fundId") UUID fundId);

    @Query("""
            SELECT COUNT(DISTINCT f.fund.fundId)
            FROM FundOperation f
            WHERE f.child.childId = :childId
            """)
    long countDistinctFundsByChildId(@Param("childId") UUID childId);

    @Query("""
            SELECT COUNT(DISTINCT f.fund.fundId)
            FROM FundOperation f
            WHERE f.fund.schoolClass.schoolClassId = :schoolClassId
            """)
    long countDistinctFundsBySchoolClassId(@Param("schoolClassId") UUID schoolClassId);

}

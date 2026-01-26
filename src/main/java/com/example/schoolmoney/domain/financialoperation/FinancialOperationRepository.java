package com.example.schoolmoney.domain.financialoperation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FinancialOperationRepository extends JpaRepository<FinancialOperation, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM (
                        SELECT fund_operation_id AS operationId,
                           started_at AS startedAt,
                           processed_at AS processedAt,
                           amount_in_cents AS amountInCents,
                           operation_type AS operationType,
                           operation_status AS operationStatus
                        FROM fund_operations fo
                        WHERE parent_id = :parent_id
                    
                        UNION ALL
                    
                        SELECT wallet_operation_id AS operationId,
                           started_at AS startedAt,
                           processed_at AS processedAt,
                           amount_in_cents AS amountInCents,
                           operation_type AS operationType,
                           operation_status AS operationStatus
                        FROM wallet_operations
                        WHERE wallet_id = :wallet_id
                    ) AS combined
                    ORDER BY COALESCE(processedAt, startedAt) DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT fund_operation_id AS id
                        FROM fund_operations
                        WHERE parent_id = :parent_id
                    
                        UNION ALL
                    
                        SELECT wallet_operation_id AS id
                        FROM wallet_operations
                        WHERE wallet_id = :wallet_id
                    ) AS combined
                    """,
            nativeQuery = true
    )
    Page<FinancialOperationView> findFinancialOperations(
            @Param("parent_id") UUID parentId,
            @Param("wallet_id") UUID walletId,
            Pageable pageable
    );

}

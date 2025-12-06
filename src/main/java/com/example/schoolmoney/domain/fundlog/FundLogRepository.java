package com.example.schoolmoney.domain.fundlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FundLogRepository extends JpaRepository<FundLog, Long> {

    @Query(
            value = """
                    SELECT processed_at AS timestamp,
                           f.title AS fund_title,
                           CONCAT(u.first_name, ' ', u.last_name) AS parent_full_name,
                           CONCAT(c.first_name, ' ', c.last_name) AS child_full_name,
                           amount_in_cents AS amount_in_cents,
                           fund_operations.currency AS currency,
                           operation_type AS operation_type,
                           operation_status AS operation_status,
                           '' AS description
                    FROM fund_operations
                    JOIN funds f on fund_operations.fund_id = f.fund_id
                    JOIN children c on fund_operations.child_id = c.child_id
                    JOIN parents p on fund_operations.parent_id = p.parent_id
                    JOIN users u on p.parent_id = u.user_id
                    WHERE fund_operations.fund_id = :fund_id
                    UNION ALL
                    SELECT ignored_at AS timestamp,
                           f.title AS fund_title,
                           CONCAT(u.first_name, ' ', u.last_name) AS parent_full_name,
                           CONCAT(c.first_name, ' ', c.last_name) AS child_full_name,
                           0.0 AS amount_in_cents,
                           '' AS currency,
                           'FUND_REJECTED' AS operation_type,
                           '' AS operation_status,
                           '' AS description
                    FROM child_ignored_funds
                    JOIN funds f on child_ignored_funds.fund_id = f.fund_id
                    JOIN children c on child_ignored_funds.child_id = c.child_id
                    JOIN parents p on c.parent_id = p.parent_id
                    JOIN users u on p.parent_id = u.user_id
                    WHERE child_ignored_funds.fund_id = :fund_id
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT fund_operation_id AS id
                        FROM fund_operations
                        WHERE fund_id = :fund_id
                        UNION ALL
                        SELECT (child_id, fund_id) AS id
                        FROM child_ignored_funds
                        WHERE fund_id = :fund_id
                    ) AS combined
                    """,
            nativeQuery = true
    )
    Page<FundLogView> findFundLogs(@Param("fund_id") UUID fundId, Pageable pageable);

}

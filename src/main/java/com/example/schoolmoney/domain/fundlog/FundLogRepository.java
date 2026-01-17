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
                    SELECT fo.processed_at AS timestamp,
                           f.title AS fund_title,
                           NULLIF(CONCAT_WS(' ', u.first_name, u.last_name), '') AS parent_full_name,
                           NULLIF(CONCAT_WS(' ', c.first_name, c.last_name), '') AS child_full_name,
                           fo.amount_in_cents AS amount_in_cents,
                           fo.currency AS currency,
                           fo.operation_type AS operation_type,
                           fo.operation_status AS operation_status,
                           fo.note AS note
                    FROM fund_operations fo
                    JOIN funds f ON fo.fund_id = f.fund_id
                    LEFT JOIN children c ON fo.child_id = c.child_id
                    JOIN parents p ON fo.parent_id = p.parent_id
                    JOIN users u ON p.parent_id = u.user_id
                    WHERE (
                        :fundId IS NOT NULL
                        AND f.fund_id = :fundId
                    ) OR (
                        :fundId IS NULL
                        AND f.school_class_id = :schoolClassId
                        AND f.fund_status = :fundStatus
                    )
                    
                    UNION ALL
                    
                    SELECT cif.ignored_at AS timestamp,
                           f.title AS fund_title,
                           NULLIF(CONCAT_WS(' ', u.first_name, u.last_name), '') AS parent_full_name,
                           NULLIF(CONCAT_WS(' ', c.first_name, c.last_name), '') AS child_full_name,
                           NULL AS amount_in_cents,
                           NULL AS currency,
                           'FUND_REJECTION' AS operation_type,
                           'SUCCESS' AS operation_status,
                           NULL AS note
                    FROM child_ignored_funds cif
                    JOIN funds f ON cif.fund_id = f.fund_id
                    JOIN children c ON cif.child_id = c.child_id
                    JOIN parents p ON c.parent_id = p.parent_id
                    JOIN users u ON p.parent_id = u.user_id
                    WHERE (
                        :fundId IS NOT NULL
                        AND f.fund_id = :fundId
                    ) OR (
                        :fundId IS NULL
                        AND f.school_class_id = :schoolClassId
                        AND f.fund_status = :fundStatus
                    )
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT fo.fund_operation_id
                        FROM fund_operations fo
                        JOIN funds f ON fo.fund_id = f.fund_id
                        WHERE (
                            :fundId IS NOT NULL
                            AND f.fund_id = :fundId
                        ) OR (
                            :fundId IS NULL
                            AND f.school_class_id = :schoolClassId
                            AND f.fund_status = :fundStatus
                        )
                    
                        UNION ALL
                    
                        SELECT cif.child_id
                        FROM child_ignored_funds cif
                        JOIN funds f ON cif.fund_id = f.fund_id
                        WHERE (
                            :fundId IS NOT NULL
                            AND f.fund_id = :fundId
                        ) OR (
                            :fundId IS NULL
                            AND f.school_class_id = :schoolClassId
                            AND f.fund_status = :fundStatus
                        )
                    ) combined
                    """,
            nativeQuery = true
    )
    Page<FundLogView> findFundLogs(
            @Param("fundId") UUID fundId,
            @Param("schoolClassId") UUID schoolClassId,
            @Param("fundStatus") String fundStatus,
            Pageable pageable
    );

}

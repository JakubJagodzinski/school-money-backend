package com.example.schoolmoney.domain.childfund;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChildFundRepository extends JpaRepository<ChildFund, Long> {

    @Query(
            value = """
                    SELECT
                        c.child_id AS childId,
                        f.fund_id AS fundId,
                        CASE
                            WHEN fo.fund_id IS NOT NULL THEN 'PAID'
                            WHEN cif.fund_id IS NOT NULL THEN 'IGNORED'
                            WHEN f.fund_status NOT IN ('ACTIVE','SCHEDULED') THEN 'UNPAID'
                        END AS childStatus
                    FROM children c
                    JOIN school_classes sc ON sc.school_class_id = c.school_class_id
                    JOIN funds f ON f.school_class_id = sc.school_class_id
                    LEFT JOIN fund_operations fo
                           ON fo.child_id = c.child_id
                           AND fo.fund_id = f.fund_id
                           AND fo.operation_status = 'SUCCESS'
                    LEFT JOIN child_ignored_funds cif
                           ON cif.child_id = c.child_id
                           AND cif.fund_id = f.fund_id
                    WHERE c.parent_id = :parentId
                      AND sc.school_class_id = :schoolClassId
                      AND (
                            fo.fund_id IS NOT NULL
                            OR cif.fund_id IS NOT NULL
                            OR f.fund_status NOT IN ('ACTIVE','SCHEDULED')
                          )
                    ORDER BY c.child_id, f.fund_id
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM children c
                    JOIN school_classes sc ON sc.school_class_id = c.school_class_id
                    JOIN funds f ON f.school_class_id = sc.school_class_id
                    LEFT JOIN fund_operations fo
                           ON fo.child_id = c.child_id
                           AND fo.fund_id = f.fund_id
                           AND fo.operation_status = 'SUCCESS'
                    LEFT JOIN child_ignored_funds cif
                           ON cif.child_id = c.child_id
                           AND cif.fund_id = f.fund_id
                    WHERE c.parent_id = :parentId
                      AND sc.school_class_id = :schoolClassId
                      AND (
                            fo.fund_id IS NOT NULL
                            OR cif.fund_id IS NOT NULL
                            OR f.fund_status NOT IN ('ACTIVE','SCHEDULED')
                          )
                    """,
            nativeQuery = true
    )
    Page<ChildFundView> findSchoolClassParentChildrenFundsHistory(
            @Param("parentId") UUID parentId,
            @Param("schoolClassId") UUID schoolClassId,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT
                        c.child_id AS childId,
                        f.fund_id AS fundId
                    FROM children c
                    JOIN school_classes sc ON sc.school_class_id = c.school_class_id
                    JOIN funds f ON f.school_class_id = sc.school_class_id
                    WHERE c.parent_id = :parentId
                      AND sc.school_class_id = :schoolClassId
                      AND f.fund_status = 'ACTIVE'
                      AND NOT EXISTS (
                          SELECT 1
                          FROM fund_operations fo
                          WHERE fo.child_id = c.child_id
                            AND fo.fund_id = f.fund_id
                            AND fo.operation_status = 'SUCCESS'
                      )
                    ORDER BY c.child_id, f.fund_id
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM children c
                    JOIN school_classes sc ON sc.school_class_id = c.school_class_id
                    JOIN funds f ON f.school_class_id = sc.school_class_id
                    WHERE c.parent_id = :parentId
                      AND sc.school_class_id = :schoolClassId
                      AND f.fund_status = 'ACTIVE'
                      AND NOT EXISTS (
                          SELECT 1
                          FROM fund_operations fo
                          WHERE fo.child_id = c.child_id
                            AND fo.fund_id = f.fund_id
                            AND fo.operation_status = 'SUCCESS'
                      )
                    """,
            nativeQuery = true
    )
    Page<ChildFundView> findSchoolClassParentChildrenUnpaidFunds(
            @Param("parentId") UUID parentId,
            @Param("schoolClassId") UUID schoolClassId,
            Pageable pageable
    );

}

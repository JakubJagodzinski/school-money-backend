package com.example.schoolmoney.domain.childignoredfund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ChildIgnoredFundRepository extends JpaRepository<ChildIgnoredFund, ChildIgnoredFundId> {

    void deleteByChild_ChildIdAndFund_FundId(UUID childId, UUID fundId);

    @Query("""
                SELECT cif.child.childId
                FROM ChildIgnoredFund cif
                WHERE cif.fund.fundId = :fundId
                  AND cif.child.parent.userId = :parentId
            """)
    Set<UUID> findParentFundIgnoredChildrenIds(
            @Param("fundId") UUID fundId,
            @Param("parentId") UUID parentId
    );

    @Query("""
                SELECT cif.child.childId
                FROM ChildIgnoredFund cif
                WHERE cif.fund.fundId = :fundId
            """)
    Set<UUID> findFundAllIgnoredChildrenIds(
            @Param("fundId") UUID fundId
    );

}

package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.domain.parent.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, UUID> {

    Page<Child> findAllBySchoolClass_SchoolClassId(UUID schoolClassId, Pageable pageable);

    List<Child> findAllBySchoolClass_SchoolClassId(UUID schoolClassId);

    boolean existsByParent_UserIdAndSchoolClass_SchoolClassId(UUID userId, UUID schoolClassId);

    Page<Child> findAllByParent_UserId(UUID userId, Pageable pageable);

    @Query("""
                SELECT DISTINCT c.schoolClass.schoolClassId
                FROM Child c
                WHERE c.parent.userId = :userId
            """)
    List<UUID> findDistinctSchoolClassIdsByParentUserId(@Param("userId") UUID userId);

    @Query("""
                SELECT DISTINCT c.parent
                FROM Child c
                WHERE c.schoolClass IS NOT NULL AND c.schoolClass.schoolClassId = :schoolClassId
            """)
    List<Parent> findSchoolClassDistinctParents(@Param("schoolClassId") UUID schoolClassId);

    long countBySchoolClass_SchoolClassId(UUID schoolClassId);

}

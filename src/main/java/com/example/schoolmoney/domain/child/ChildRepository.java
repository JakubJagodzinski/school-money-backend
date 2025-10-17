package com.example.schoolmoney.domain.child;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, UUID> {

    Page<Child> findAllBySchoolClass_SchoolClassId(UUID schoolClassId, Pageable pageable);

    boolean existsByParent_UserIdAndSchoolClass_SchoolClassId(UUID userId, UUID schoolClassId);

    Page<Child> findAllByParent_UserId(UUID userId, Pageable pageable);

}

package com.example.schoolmoney.domain.fund;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface FundRepository extends JpaRepository<Fund, UUID> {

    List<Fund> findAllByEndsAtBeforeAndFundStatus(Instant now, FundStatus fundStatus);

    Page<Fund> findAllByAuthor_UserId(UUID userId, Pageable pageable);

    long countBySchoolClass_SchoolClassIdAndFundStatus(UUID schoolClassId, FundStatus fundStatus);

    Page<Fund> findAllBySchoolClass_SchoolClassId(UUID schoolClassId, Pageable pageable);

}

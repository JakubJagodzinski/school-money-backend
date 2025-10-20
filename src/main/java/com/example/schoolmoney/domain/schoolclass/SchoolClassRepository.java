package com.example.schoolmoney.domain.schoolclass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {

    Optional<SchoolClass> findByInvitationCode(String invitationCode);

    Page<SchoolClass> findAllByTreasurer_UserIdOrSchoolClassIdIn(UUID treasurerId, List<UUID> schoolClassId, Pageable pageable);

}

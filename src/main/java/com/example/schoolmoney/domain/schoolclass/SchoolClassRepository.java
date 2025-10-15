package com.example.schoolmoney.domain.schoolclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {
}

package com.example.schoolmoney.domain.fund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FundRepository extends JpaRepository<Fund, UUID> {
}

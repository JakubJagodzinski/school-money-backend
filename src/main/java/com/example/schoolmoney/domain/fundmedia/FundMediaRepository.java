package com.example.schoolmoney.domain.fundmedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FundMediaRepository extends JpaRepository<FundMedia, UUID> {
}

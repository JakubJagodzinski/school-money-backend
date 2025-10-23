package com.example.schoolmoney.domain.fundmedia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FundMediaRepository extends JpaRepository<FundMedia, UUID> {

    Page<FundMedia> findAllByFund_FundId(UUID fundId, Pageable pageable);

}

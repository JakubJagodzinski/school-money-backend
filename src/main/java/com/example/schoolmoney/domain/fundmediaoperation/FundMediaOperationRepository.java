package com.example.schoolmoney.domain.fundmediaoperation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FundMediaOperationRepository extends JpaRepository<FundMediaOperation, UUID> {

    Page<FundMediaOperation> findAllByFundId(UUID fundId, Pageable pageable);

}

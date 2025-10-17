package com.example.schoolmoney.domain.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    boolean existsByParent_UserId(UUID userId);

    Wallet findByParent_UserId(UUID userId);

}

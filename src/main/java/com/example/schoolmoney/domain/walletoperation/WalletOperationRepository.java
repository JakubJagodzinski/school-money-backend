package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.payment.PaymentProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletOperationRepository extends JpaRepository<WalletOperation, UUID> {

    boolean existsByExternalPaymentIdAndPaymentProviderType(String externalPaymentId, PaymentProviderType paymentProviderType);

    Page<WalletOperation> findAllByWallet_WalletIdOrderByProcessedAtDesc(UUID walletId, Pageable pageable);

}

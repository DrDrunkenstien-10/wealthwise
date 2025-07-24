package com.wealthwise.receipt.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wealthwise.receipt.model.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    @Query("SELECT r.filePath FROM Receipt r WHERE r.transaction.transactionId = :transactionId")
    Optional<String> findFilePathByTransactionId(@Param("transactionId") UUID transactionId);
}

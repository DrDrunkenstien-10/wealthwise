package com.wealthwise.receipt.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class ReceiptRequestDTO {

    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;

    // Getters and setters
    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
}

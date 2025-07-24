package com.wealthwise.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.wealthwise.transaction.enums.TransactionType;

public class TransactionFilterDTO {
    private String transactionName;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String category;
    private String paymentType;
    private TransactionType transactionType;
    private LocalDateTime date;

    // Constructor
    public TransactionFilterDTO(String transactionName, BigDecimal minAmount, BigDecimal maxAmount, String category,
            String paymentType, TransactionType transactionType, LocalDateTime date) {
        this.transactionName = transactionName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.category = category;
        this.paymentType = paymentType;
        this.transactionType = transactionType;
        this.date = date;
    }

    // Getters and setters
    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

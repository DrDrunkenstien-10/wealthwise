package com.wealthwise.recurringtransaction.dto;

import java.math.BigDecimal;

import com.wealthwise.recurringtransaction.enums.Frequency;
import com.wealthwise.transaction.enums.TransactionType;

public class RecurringTransactionFilterDTO {
    private String recurringTransactionName;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String category;
    private String paymentType;
    private TransactionType transactionType;
    private Boolean isActive;
    private Frequency frequency;

    // Constructor
    public RecurringTransactionFilterDTO(String recurringTransactionName, BigDecimal minAmount, BigDecimal maxAmount,
            String category, String paymentType, TransactionType transactionType, Boolean isActive,
            Frequency frequency) {
        this.recurringTransactionName = recurringTransactionName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.category = category;
        this.paymentType = paymentType;
        this.transactionType = transactionType;
        this.isActive = isActive;
        this.frequency = frequency;
    }

    // Getters and setters
    public String getRecurringTransactionName() {
        return recurringTransactionName;
    }

    public void setRecurringTransactionName(String recurringTransactionName) {
        this.recurringTransactionName = recurringTransactionName;
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

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
}

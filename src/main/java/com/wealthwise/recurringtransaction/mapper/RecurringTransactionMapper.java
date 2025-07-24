package com.wealthwise.recurringtransaction.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import com.wealthwise.recurringtransaction.dto.RecurringTransactionRequestDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionResponseDTO;
import com.wealthwise.recurringtransaction.model.RecurringTransaction;
import com.wealthwise.transaction.dto.TransactionRequestDTO;
import com.wealthwise.user.model.User;

public class RecurringTransactionMapper {
    public static RecurringTransaction toModel(
            RecurringTransactionRequestDTO recurringTransactionRequestDTO, User user) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();

        recurringTransaction.setUser(user);
        recurringTransaction.setRecurringTransactionName(recurringTransactionRequestDTO.getRecurringTransactionName());
        recurringTransaction.setDescription(recurringTransactionRequestDTO.getDescription());
        recurringTransaction.setAmount(recurringTransactionRequestDTO.getAmount());
        recurringTransaction.setCategory(recurringTransactionRequestDTO.getCategory());
        recurringTransaction.setPaymentType(recurringTransactionRequestDTO.getPaymentType());
        recurringTransaction.setTransactionType(recurringTransactionRequestDTO.getTransactionType());
        recurringTransaction.setFrequency(recurringTransactionRequestDTO.getFrequency());
        recurringTransaction.setStartDate(recurringTransactionRequestDTO.getStartDate());
        recurringTransaction.setEndDate(recurringTransactionRequestDTO.getEndDate());
        recurringTransaction.setNextOccurrence(recurringTransactionRequestDTO.getNextOccurrence());
        recurringTransaction.setIsActive(true);

        return recurringTransaction;
    }

    public static RecurringTransactionResponseDTO toDTO(RecurringTransaction recurringTransaction) {
        RecurringTransactionResponseDTO recurringTransactionResponseDTO = new RecurringTransactionResponseDTO();

        recurringTransactionResponseDTO.setRecurringTransactionId(recurringTransaction.getRecurringTransactionId());
        recurringTransactionResponseDTO.setUserId(recurringTransaction.getUser().getUserId());
        recurringTransactionResponseDTO.setRecurringTransactionName(recurringTransaction.getRecurringTransactionName());
        recurringTransactionResponseDTO.setDescription(recurringTransaction.getDescription());
        recurringTransactionResponseDTO.setAmount(recurringTransaction.getAmount());
        recurringTransactionResponseDTO.setCategory(recurringTransaction.getCategory());
        recurringTransactionResponseDTO.setPaymentType(recurringTransaction.getPaymentType());
        recurringTransactionResponseDTO.setTransactionType(recurringTransaction.getTransactionType());
        recurringTransactionResponseDTO.setFrequency(recurringTransaction.getFrequency());
        recurringTransactionResponseDTO.setStartDate(recurringTransaction.getStartDate());
        recurringTransactionResponseDTO.setEndDate(recurringTransaction.getEndDate());
        recurringTransactionResponseDTO.setNextOccurence(recurringTransaction.getNextOccurrence());
        recurringTransactionResponseDTO.setIsActive(recurringTransaction.getIsActive());
        recurringTransactionResponseDTO.setCreatedAt(recurringTransaction.getCreatedAt());
        recurringTransactionResponseDTO.setUpdatedAt(recurringTransaction.getUpdatedAt());

        return recurringTransactionResponseDTO;
    }

    public static TransactionRequestDTO toTransactionRequestDTO(
            RecurringTransactionResponseDTO recurringTransactionResponseDTO, 
            UUID userId) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();

        transactionRequestDTO.setUserId(userId);
        transactionRequestDTO.setName(recurringTransactionResponseDTO.getRecurringTransactionName());
        transactionRequestDTO.setDescription(recurringTransactionResponseDTO.getDescription());
        transactionRequestDTO.setAmount(recurringTransactionResponseDTO.getAmount());
        transactionRequestDTO.setCategory(recurringTransactionResponseDTO.getCategory());
        transactionRequestDTO.setPaymentType(recurringTransactionResponseDTO.getPaymentType());
        transactionRequestDTO.setDate(LocalDateTime.now());
        transactionRequestDTO.setTransactionType(recurringTransactionResponseDTO.getTransactionType());

        return transactionRequestDTO;
    }
}

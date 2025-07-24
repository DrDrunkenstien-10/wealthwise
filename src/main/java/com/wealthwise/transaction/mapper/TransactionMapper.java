package com.wealthwise.transaction.mapper;

import com.wealthwise.transaction.dto.TransactionRequestDTO;
import com.wealthwise.transaction.dto.TransactionResponseDTO;
import com.wealthwise.transaction.model.Transaction;
import com.wealthwise.user.model.User;

public class TransactionMapper {
    public static Transaction toModel(TransactionRequestDTO transactionRequestDTO, User user) {
        Transaction transaction = new Transaction();

        transaction.setUser(user);
        transaction.setName(transactionRequestDTO.getName());
        transaction.setDescription(transactionRequestDTO.getDescription());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setCategory(transactionRequestDTO.getCategory());
        transaction.setPaymentType(transactionRequestDTO.getPaymentType());
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setTransactionType(transactionRequestDTO.getTransactionType());

        return transaction;
    }

    public static TransactionResponseDTO toDTO(Transaction transaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();

        transactionResponseDTO.setTransactionId(transaction.getTransactionId());
        transactionResponseDTO.setUserId(transaction.getUser().getUserId());
        transactionResponseDTO.setName(transaction.getName());
        transactionResponseDTO.setDescription(transaction.getDescription());
        transactionResponseDTO.setAmount(transaction.getAmount());
        transactionResponseDTO.setCategory(transaction.getCategory());
        transactionResponseDTO.setPaymentType(transaction.getPaymentType());
        transactionResponseDTO.setDate(transaction.getDate());
        transactionResponseDTO.setCreatedAt(transaction.getCreatedAt());
        transactionResponseDTO.setUpdatedAt(transaction.getUpdatedAt());
        transactionResponseDTO.setTransactionType(transaction.getTransactionType());

        return transactionResponseDTO;
    }
}

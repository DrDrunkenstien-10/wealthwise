package com.wealthwise.transaction.validator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.wealthwise.transaction.exception.TransactionNotFoundException;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.repository.TransactionRepository;
import com.wealthwise.user.repository.UserRepository;

@Component
public class TransactionValidator {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionValidator(UserRepository userRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void validateTransactionCreationInput(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User not found. User ID: " + userId);
        }
    }

    public void validateForReadTransactionByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User not found. User ID: " + userId);
        }
    }

    public void validateTransactionExistence(UUID transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new TransactionNotFoundException(
                    "Transaction not found. Transaction Id: " + transactionId);
        }
    }
}

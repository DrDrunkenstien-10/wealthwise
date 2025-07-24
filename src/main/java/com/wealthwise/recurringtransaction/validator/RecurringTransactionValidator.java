package com.wealthwise.recurringtransaction.validator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.user.repository.UserRepository;

@Component
public class RecurringTransactionValidator {
    private final UserRepository userRepository;

    public RecurringTransactionValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateForReadRecurringTransactionByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User not found. User ID: " + userId);
        }
    }
}

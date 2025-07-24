package com.wealthwise.recurringtransaction.exception;

public class DuplicateRecurringTransactionException extends RuntimeException {
    public DuplicateRecurringTransactionException(String message) {
        super(message);
    }
}

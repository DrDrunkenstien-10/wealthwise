package com.wealthwise.receipt.exception;

public class MaxFileSizeLimitExceededException extends RuntimeException {
    public MaxFileSizeLimitExceededException(String message) {
        super(message);
    }
}

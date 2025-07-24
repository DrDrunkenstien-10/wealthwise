package com.wealthwise.receipt.exception;

public class EmptyReceiptFoundException extends RuntimeException {
    public EmptyReceiptFoundException(String message) {
        super(message);
    }
}

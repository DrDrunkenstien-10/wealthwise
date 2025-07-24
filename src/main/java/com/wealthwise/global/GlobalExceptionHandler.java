package com.wealthwise.global;

import java.util.HashMap;
import java.util.Map;

import com.wealthwise.receipt.exception.EmptyReceiptFoundException;
import com.wealthwise.receipt.exception.InvalidContentTypeException;
import com.wealthwise.receipt.exception.MaxFileSizeLimitExceededException;
import com.wealthwise.receipt.exception.ReceiptNotFoundException;
import com.wealthwise.recurringtransaction.exception.RecurringTransactionNotFoundException;
import com.wealthwise.transaction.exception.TransactionNotFoundException;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.exception.DuplicateTransactionException;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("traceId={} | Validation failed: {}",
                ThreadContext.get("traceId"), ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return logAndBuildError("User not found", ex, 400, false);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        return logAndBuildError("Transaction not found", ex, 400, false);
    }

    @ExceptionHandler(EmptyReceiptFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmptyReceiptFoundException(EmptyReceiptFoundException ex) {
        return logAndBuildError("The uploaded file is empty or has no content", ex, 400, false);
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidContentTypeException(InvalidContentTypeException ex) {
        return logAndBuildError("Only PDF, JPG, and PNG files are allowed", ex, 400, false);
    }

    @ExceptionHandler(MaxFileSizeLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxFileSizeLimitExceededException(
            MaxFileSizeLimitExceededException ex) {
        return logAndBuildError("Max file size limit exceeded", ex, 400, false);
    }

    @ExceptionHandler(RecurringTransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRecurringTransactionNotFoundException(
            RecurringTransactionNotFoundException ex) {
        return logAndBuildError("Recurring transaction not found", ex, 400, false);
    }

    @ExceptionHandler(ReceiptNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReceiptNotFoundException(ReceiptNotFoundException ex) {
        return logAndBuildError("Receipt not found", ex, 400, false);
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateTransactionException(DuplicateTransactionException ex) {
        return logAndBuildError(ex.getMessage(), ex, 409, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return logAndBuildError("An unexpected error occurred", ex, 500, true);
    }

    // 🔁 Shared method for structured logging and response building
    private ResponseEntity<Map<String, String>> logAndBuildError(String message, Exception ex, int statusCode,
            boolean isServerError) {
        String traceId = ThreadContext.get("traceId");

        if (isServerError) {
            log.error("traceId={} | {}: {}", traceId, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        } else {
            log.warn("traceId={} | {}: {}", traceId, ex.getClass().getSimpleName(), ex.getMessage());
        }

        Map<String, String> error = new HashMap<>();
        error.put("message", message);

        return ResponseEntity.status(statusCode).body(error);
    }
}

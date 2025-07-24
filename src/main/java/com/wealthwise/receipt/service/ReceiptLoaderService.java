package com.wealthwise.receipt.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wealthwise.receipt.exception.ReceiptNotFoundException;
import com.wealthwise.receipt.repository.ReceiptRepository;

@Service
public class ReceiptLoaderService {
    private final ReceiptRepository receiptRepository;

    public ReceiptLoaderService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public byte[] loadReceipt(UUID transactionId) throws IOException {
        String filePath = receiptRepository.findFilePathByTransactionId(transactionId)
                .orElseThrow(
                        () -> new ReceiptNotFoundException("Receipt not found for transaction ID: " + transactionId));

        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
}

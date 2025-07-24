package com.wealthwise.receipt.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wealthwise.transaction.exception.TransactionNotFoundException;
import com.wealthwise.transaction.repository.TransactionRepository;

@Service
public class ReceiptViewService {

    private final ReceiptLoaderService receiptLoaderService;
    private final ReceiptService receiptService;
    private final TransactionRepository transactionRepository;

    public ReceiptViewService(ReceiptLoaderService receiptLoaderService,
            ReceiptService receiptService,
            TransactionRepository transactionRepository) {
        this.receiptLoaderService = receiptLoaderService;
        this.receiptService = receiptService;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<byte[]> prepareReceiptResponse(UUID transactionId, UUID userId, boolean download)
            throws IOException {

        transactionRepository
                .findByTransactionIdAndUserId(transactionId, userId)
                .orElseThrow(
                        () -> new TransactionNotFoundException("Transaction not found or does not belong to user"));

        byte[] data = receiptLoaderService.loadReceipt(transactionId);
        String fileExtension = receiptService.readReceiptFileExtension(transactionId);

        MediaType mediaType = resolveMediaType(fileExtension);
        String fileName = "Receipt" + (fileExtension != null ? "." + fileExtension : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(download ? ContentDisposition.attachment().filename(fileName).build()
                : ContentDisposition.inline().filename(fileName).build());
        headers.setContentLength(data.length);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private MediaType resolveMediaType(String fileExtension) {
        if (fileExtension == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}

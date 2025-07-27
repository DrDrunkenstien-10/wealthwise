package com.wealthwise.receipt.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wealthwise.receipt.dto.ReceiptRequestDTO;
import com.wealthwise.receipt.dto.ReceiptResponseDTO;
import com.wealthwise.receipt.service.ReceiptService;
import com.wealthwise.receipt.service.ReceiptViewService;
import com.wealthwise.user.service.CurrentUserProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/receipt")
public class ReceiptController {
    private final ReceiptService receiptService;
    private final CurrentUserProvider currentUserProvider;
    private final ReceiptViewService receiptViewService;

    public ReceiptController(ReceiptService receiptService,
            CurrentUserProvider currentUserProvider,
            ReceiptViewService receiptViewService) {
        this.receiptService = receiptService;
        this.currentUserProvider = currentUserProvider;
        this.receiptViewService = receiptViewService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReceiptResponseDTO> saveReceipt(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("receipt") MultipartFile receipt,
            @RequestPart("metadata") @Valid @NotNull ReceiptRequestDTO receiptRequestDTO) {

        UUID userId = currentUserProvider.getCurrentUserId(jwt);

        ReceiptResponseDTO response = receiptService.uploadReceipt(receipt, receiptRequestDTO, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/view/{transactionId}")
    public ResponseEntity<byte[]> viewReceipt(
            @PathVariable("transactionId") UUID transactionId,
            @AuthenticationPrincipal Jwt jwt) throws IOException {

        UUID userId = currentUserProvider.getCurrentUserId(jwt);
        return receiptViewService.prepareReceiptResponse(transactionId, userId, false);
    }

    @GetMapping("/download/{transactionId}")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable("transactionId") UUID transactionId,
            @AuthenticationPrincipal Jwt jwt) throws IOException {

        UUID userId = currentUserProvider.getCurrentUserId(jwt);
        return receiptViewService.prepareReceiptResponse(transactionId, userId, true);
    }
}

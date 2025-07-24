package com.wealthwise.receipt.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wealthwise.receipt.dto.ReceiptRequestDTO;
import com.wealthwise.receipt.dto.ReceiptResponseDTO;
import com.wealthwise.receipt.exception.ReceiptNotFoundException;
import com.wealthwise.receipt.mapper.ReceiptMapper;
import com.wealthwise.receipt.model.Receipt;
import com.wealthwise.receipt.repository.ReceiptRepository;
import com.wealthwise.receipt.validator.ReceiptValidator;
import com.wealthwise.transaction.exception.TransactionNotFoundException;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.model.Transaction;
import com.wealthwise.transaction.repository.TransactionRepository;
import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;

@Service
public class ReceiptService {
    private final ReceiptValidator receiptValidator;
    private final ReceiptStorageService receiptStorageService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptValidator receiptValidator,
            ReceiptStorageService receiptStorageService,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            ReceiptRepository receiptRepository) {
        this.receiptValidator = receiptValidator;
        this.receiptStorageService = receiptStorageService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.receiptRepository = receiptRepository;
    }

    // Need to handle edge case where file is saved to disk, but the database operation to store the 
    // file metadata fails. 
    public ReceiptResponseDTO uploadReceipt(MultipartFile receiptFile,
            ReceiptRequestDTO receiptRequestDTO, UUID userId) {

        Transaction transaction = transactionRepository
                .findByTransactionIdAndUserId(receiptRequestDTO.getTransactionId(), userId)
                .orElseThrow(
                        () -> new TransactionNotFoundException("Transaction not found or does not belong to user"));

        receiptValidator.validateReceipt(receiptFile);

        String filePath = receiptStorageService.storeReceiptFile(receiptFile);

        String originalFilename = StringUtils.cleanPath(
                Optional.ofNullable(receiptFile.getOriginalFilename()).orElse("receipt"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found. User Id: " + userId));

        Receipt newReceipt = receiptRepository.save(ReceiptMapper.toModel(receiptRequestDTO, user,
                transaction, originalFilename, filePath));

        return ReceiptMapper.toDTO(newReceipt);
    }

    public String readReceiptFileExtension(UUID transactionId) {
        String filePath = receiptRepository.findFilePathByTransactionId(transactionId).orElseThrow(
                () -> new ReceiptNotFoundException("Receipt not found for transaction ID: " + transactionId));

        String extension = "";
        if (filePath != null && filePath.lastIndexOf('.') != -1) {
            extension = filePath.substring(filePath.lastIndexOf('.'));
        }

        return extension;
    }
}

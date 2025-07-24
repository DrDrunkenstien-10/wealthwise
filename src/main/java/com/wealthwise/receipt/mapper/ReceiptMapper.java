package com.wealthwise.receipt.mapper;

import com.wealthwise.receipt.dto.ReceiptRequestDTO;
import com.wealthwise.receipt.dto.ReceiptResponseDTO;
import com.wealthwise.receipt.model.Receipt;
import com.wealthwise.transaction.model.Transaction;
import com.wealthwise.user.model.User;

public class ReceiptMapper {
    public static Receipt toModel(ReceiptRequestDTO receiptRequestDTO, User user,
            Transaction transaction, String originalFilename, String filePath) {
        Receipt receipt = new Receipt();

        receipt.setUser(user);
        receipt.setTransaction(transaction);
        receipt.setFileName(originalFilename);
        receipt.setFilePath(filePath);

        return receipt;
    }

    public static ReceiptResponseDTO toDTO(Receipt receipt) {
        ReceiptResponseDTO receiptResponseDTO = new ReceiptResponseDTO();

        receiptResponseDTO.setReceiptId(receipt.getReceiptId());
        receiptResponseDTO.setUserId(receipt.getUser().getUserId());
        receiptResponseDTO.setTransactionId(receipt.getTransaction().getTransactionId());
        receiptResponseDTO.setFileName(receipt.getFileName());
        receiptResponseDTO.setFilePath(receipt.getFilePath());
        receiptResponseDTO.setUploadTimestamp(receipt.getUploadTimestamp());
        receiptResponseDTO.setUpdatedAt(receipt.getUpdatedAt());

        return receiptResponseDTO;
    }
}

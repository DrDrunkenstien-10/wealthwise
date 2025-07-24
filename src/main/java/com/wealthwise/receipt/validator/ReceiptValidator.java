package com.wealthwise.receipt.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.wealthwise.receipt.exception.EmptyReceiptFoundException;
import com.wealthwise.receipt.exception.InvalidContentTypeException;
import com.wealthwise.receipt.exception.MaxFileSizeLimitExceededException;

@Component
public class ReceiptValidator {
    private static final int ALLOWED_FILE_SIZE_LIMIT = 1000000;

    public void validateReceipt(MultipartFile receipt) {
        String contentType = receipt.getContentType();

        if (receipt.isEmpty()) {
            throw new EmptyReceiptFoundException(
                    "The uploaded file is is empty, that is, either no file has been chosen in the multipart form or the chosen file has no content. File name: "
                            + receipt.getOriginalFilename());
        }

        if (!contentType.equals("application/pdf") && !contentType.equals("image/jpeg")
                && !contentType.equals("image/png")) {
            throw new InvalidContentTypeException(
                    "Only PDF, JPG, and PNG files are allowed. Uploaded file content: "
                            + contentType);
        }

        if (receipt.getSize() > ALLOWED_FILE_SIZE_LIMIT) {
            throw new MaxFileSizeLimitExceededException(
                    "Max file size limit exceeded. Uploaded file size: "
                            + receipt.getSize());
        }
    }
}

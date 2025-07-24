package com.wealthwise.receipt.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReceiptStorageService {

    private final Path rootLocation;

    public ReceiptStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeReceiptFile(MultipartFile receiptFile) {
        try {
            Files.createDirectories(rootLocation);

            String originalFilename = receiptFile.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + extension;
            Path destinationFile = rootLocation.resolve(filename);

            receiptFile.transferTo(destinationFile.toFile());

            return destinationFile.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
}

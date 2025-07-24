package com.wealthwise.transaction.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wealthwise.global.dto.PaginatedResponseDTO;
import com.wealthwise.transaction.dto.TransactionFilterDTO;
import com.wealthwise.transaction.dto.TransactionPatchDTO;
import com.wealthwise.transaction.dto.TransactionRequestDTO;
import com.wealthwise.transaction.dto.TransactionResponseDTO;
import com.wealthwise.transaction.enums.TransactionType;
import com.wealthwise.transaction.service.TransactionService;
import com.wealthwise.user.service.CurrentUserProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
	private final TransactionService transactionService;
	private final CurrentUserProvider currentUserProvider;

	public TransactionController(TransactionService transactionService,
			CurrentUserProvider currentUserProvider) {
		this.transactionService = transactionService;
		this.currentUserProvider = currentUserProvider;
	}

	@PostMapping
	public ResponseEntity<TransactionResponseDTO> createTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		TransactionResponseDTO transactionResponseDTO = transactionService
				.create(transactionRequestDTO, userId);

		return ResponseEntity.ok().body(transactionResponseDTO);
	}

	@GetMapping
	public ResponseEntity<PaginatedResponseDTO<TransactionResponseDTO>> getTransactions(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		PaginatedResponseDTO<TransactionResponseDTO> response = transactionService
				.getTransactionsByUserId(userId, page, size, sortBy);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponseDTO> getTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID transactionId) {

		UUID currentUserId = currentUserProvider.getCurrentUserId(jwt);

		TransactionResponseDTO transactionResponseDTO = transactionService
				.getTransactionByIdAndUserId(transactionId, currentUserId);

		return ResponseEntity.ok().body(transactionResponseDTO);
	}

	@GetMapping("/search")
	public ResponseEntity<PaginatedResponseDTO<TransactionResponseDTO>> searchTransactions(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(name = "transactionName", required = false) String transactionName,
			@RequestParam(name = "minAmount", required = false) BigDecimal minAmount,
			@RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount,
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "paymentType", required = false) String paymentType,
			@RequestParam(name = "transactionType", required = false) TransactionType transactionType,
			@RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortBy", defaultValue = "transactionId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {

		TransactionFilterDTO filterDTO = new TransactionFilterDTO(
				transactionName,
				minAmount,
				maxAmount,
				category,
				paymentType,
				transactionType,
				date);

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		PaginatedResponseDTO<TransactionResponseDTO> response = transactionService.searchTransactions(filterDTO,
				page,
				size, sortBy, direction, userId);

		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<TransactionResponseDTO> updateTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID transactionId,
			@RequestBody TransactionPatchDTO transactionPatchDTO) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		TransactionResponseDTO transactionResponseDTO = transactionService.updateTransaction(transactionId,
				transactionPatchDTO, userId);
		return ResponseEntity.ok().body(transactionResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID transactionId) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		transactionService.deleteTransaction(transactionId, userId);

		return ResponseEntity.noContent().build();
	}
}

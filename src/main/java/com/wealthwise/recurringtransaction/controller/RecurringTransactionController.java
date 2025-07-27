package com.wealthwise.recurringtransaction.controller;

import java.math.BigDecimal;
import java.util.UUID;

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
import com.wealthwise.recurringtransaction.dto.RecurringTransactionFilterDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionPatchRequestDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionRequestDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionResponseDTO;
import com.wealthwise.recurringtransaction.enums.Frequency;
import com.wealthwise.recurringtransaction.service.RecurringTransactionService;
import com.wealthwise.transaction.enums.TransactionType;
import com.wealthwise.user.service.CurrentUserProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/recurring-transactions")
public class RecurringTransactionController {
	private final RecurringTransactionService recurringTransactionService;
	private final CurrentUserProvider currentUserProvider;

	public RecurringTransactionController(
			RecurringTransactionService recurringTransactionService,
			CurrentUserProvider currentUserProvider) {
		this.recurringTransactionService = recurringTransactionService;
		this.currentUserProvider = currentUserProvider;
	}

	@PostMapping
	public ResponseEntity<RecurringTransactionResponseDTO> createRecurringTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@RequestBody @Valid RecurringTransactionRequestDTO recurringTransactionRequestDTO) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		RecurringTransactionResponseDTO recurringTransactionResponseDTO = recurringTransactionService
				.createRecurringTransaction(recurringTransactionRequestDTO, userId);

		return ResponseEntity.ok().body(recurringTransactionResponseDTO);
	}

	@GetMapping
	public ResponseEntity<PaginatedResponseDTO<RecurringTransactionResponseDTO>> getAllRecurringTransactions(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		PaginatedResponseDTO<RecurringTransactionResponseDTO> recurringTransactionResponseDTOs = recurringTransactionService
				.getRecurringTransactionsByUserId(userId, page, size, sortBy);

		return ResponseEntity.ok().body(recurringTransactionResponseDTOs);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecurringTransactionResponseDTO> getRecurringTransactionById(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID recurringTransactionId) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		RecurringTransactionResponseDTO recurringTransactionResponseDTO = recurringTransactionService
				.readRecurringTransactionByIdAndUserId(recurringTransactionId, userId);

		return ResponseEntity.ok().body(recurringTransactionResponseDTO);
	}

	@GetMapping("/search")
	public ResponseEntity<PaginatedResponseDTO<RecurringTransactionResponseDTO>> searchRecurringTransactions(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(name = "recurringTransactionName", required = false) String recurringTransactionName,
			@RequestParam(name = "minAmount", required = false) BigDecimal minAmount,
			@RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount,
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "paymentType", required = false) String paymentType,
			@RequestParam(name = "transactionType", required = false) TransactionType transactionType,
			@RequestParam(name = "isActive", required = false) Boolean isActive,
			@RequestParam(name = "frequency", required = false) Frequency frequency,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortBy", defaultValue = "recurringTransactionId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		RecurringTransactionFilterDTO recurringTransactionFilterDTO = new RecurringTransactionFilterDTO(
				recurringTransactionName,
				minAmount,
				maxAmount,
				category,
				paymentType,
				transactionType,
				isActive,
				frequency);

		PaginatedResponseDTO<RecurringTransactionResponseDTO> response = recurringTransactionService
				.searchRecurringTransactions(recurringTransactionFilterDTO, page, size, sortBy,
						direction, userId);

		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<RecurringTransactionResponseDTO> updateRecurringTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID recurringTransactionId,
			@RequestBody @Valid RecurringTransactionPatchRequestDTO recurringTransactionPatchRequestDTO) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		RecurringTransactionResponseDTO recurringTransactionResponseDTO = recurringTransactionService
				.updateRecurringTransaction(recurringTransactionPatchRequestDTO,
						recurringTransactionId, userId);

		return ResponseEntity.ok().body(recurringTransactionResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRecurringTransaction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") UUID recurringTransactionId) {

		UUID userId = currentUserProvider.getCurrentUserId(jwt);

		recurringTransactionService.deleteRecurringTransaction(recurringTransactionId, userId);

		return ResponseEntity.noContent().build();
	}
}

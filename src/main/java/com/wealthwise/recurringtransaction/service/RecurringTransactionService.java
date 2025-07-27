package com.wealthwise.recurringtransaction.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wealthwise.global.dto.PaginatedResponseDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionFilterDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionPatchRequestDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionRequestDTO;
import com.wealthwise.recurringtransaction.dto.RecurringTransactionResponseDTO;
import com.wealthwise.recurringtransaction.exception.DuplicateRecurringTransactionException;
import com.wealthwise.recurringtransaction.exception.RecurringTransactionNotFoundException;
import com.wealthwise.recurringtransaction.mapper.RecurringTransactionMapper;
import com.wealthwise.recurringtransaction.model.RecurringTransaction;
import com.wealthwise.recurringtransaction.repository.RecurringTransactionRepository;
import com.wealthwise.recurringtransaction.specification.RecurringTransactionSpecification;
import com.wealthwise.recurringtransaction.validator.RecurringTransactionValidator;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;

@Service
public class RecurringTransactionService {

	private final UserRepository userRepository;
	private final RecurringTransactionRepository recurringTransactionRepository;
	private final NextOccurrenceCalculator nextOccurrenceCalculator;
	private final RecurringTransactionValidator recurringTransactionValidator;

	public RecurringTransactionService(UserRepository userRepository,
			RecurringTransactionRepository recurringTransactionRepository,
			NextOccurrenceCalculator nextOccurrenceCalculator,
			RecurringTransactionValidator recurringTransactionValidator) {
		this.userRepository = userRepository;
		this.recurringTransactionRepository = recurringTransactionRepository;
		this.nextOccurrenceCalculator = nextOccurrenceCalculator;
		this.recurringTransactionValidator = recurringTransactionValidator;
	}

	public RecurringTransactionResponseDTO createRecurringTransaction(
			RecurringTransactionRequestDTO recurringTransactionRequestDTO,
			UUID userId) {

		if (recurringTransactionRepository.existsByUserIdAndRecurringTransactionName(userId,
				recurringTransactionRequestDTO.getRecurringTransactionName())) {
			throw new DuplicateRecurringTransactionException(
					"You already have a recurring transaction with this name.");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found."));

		LocalDate nextOccurrence = nextOccurrenceCalculator.calculateNextOccurrence(
				recurringTransactionRequestDTO.getStartDate(),
				recurringTransactionRequestDTO.getFrequency());

		recurringTransactionRequestDTO.setNextOccurrence(nextOccurrence);

		RecurringTransaction newRecurringTransaction = recurringTransactionRepository
				.save(RecurringTransactionMapper.toModel(recurringTransactionRequestDTO, user));

		return RecurringTransactionMapper.toDTO(newRecurringTransaction);
	}

	public PaginatedResponseDTO<RecurringTransactionResponseDTO> getRecurringTransactionsByUserId(
			UUID userId, int page, int size, String sortBy) {

		recurringTransactionValidator.validateForReadRecurringTransactionByUserId(userId);

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

		Page<RecurringTransaction> recurringTransactions = recurringTransactionRepository
				.findByUser_UserId(userId, pageable);

		List<RecurringTransactionResponseDTO> recurringTransactionResponseDTOs = recurringTransactions
				.stream()
				.map(recurringTransaction -> RecurringTransactionMapper.toDTO(recurringTransaction))
				.toList();

		return new PaginatedResponseDTO<>(
				recurringTransactionResponseDTOs,
				recurringTransactions.getNumber(),
				recurringTransactions.getSize(),
				recurringTransactions.getTotalElements(),
				recurringTransactions.getTotalPages(),
				recurringTransactions.isLast(),
				recurringTransactions.isFirst());
	}

	public RecurringTransactionResponseDTO readRecurringTransactionByIdAndUserId(
			UUID recurringTransactionId,
			UUID userId) {

		RecurringTransaction recurringTransaction = recurringTransactionRepository
				.findByRecurringTransactionIdAndUser_UserId(recurringTransactionId, userId)
				.orElseThrow(() -> new RecurringTransactionNotFoundException(
						"Recurring transaction not found. Id: " + recurringTransactionId));

		return RecurringTransactionMapper.toDTO(recurringTransaction);
	}

	public RecurringTransactionResponseDTO updateRecurringTransaction(
			RecurringTransactionPatchRequestDTO dto,
			UUID recurringTransactionId,
			UUID userId) {

		RecurringTransaction recurringTransaction = recurringTransactionRepository
				.findByRecurringTransactionIdAndUser_UserId(recurringTransactionId, userId)
				.orElseThrow(() -> new RecurringTransactionNotFoundException(
						"Recurring transaction not found. Id: " + recurringTransactionId));

		if (dto.getRecurringTransactionName() != null) {
			recurringTransaction.setRecurringTransactionName(dto.getRecurringTransactionName());
		}
		if (dto.getDescription() != null) {
			recurringTransaction.setDescription(dto.getDescription());
		}
		if (dto.getAmount() != null) {
			recurringTransaction.setAmount(dto.getAmount());
		}
		if (dto.getCategory() != null) {
			recurringTransaction.setCategory(dto.getCategory());
		}
		if (dto.getPaymentType() != null) {
			recurringTransaction.setPaymentType(dto.getPaymentType());
		}
		if (dto.getTransactionType() != null) {
			recurringTransaction.setTransactionType(dto.getTransactionType());
		}
		if (dto.getFrequency() != null) {
			recurringTransaction.setFrequency(dto.getFrequency());
		}
		if (dto.getStartDate() != null) {
			recurringTransaction.setStartDate(dto.getStartDate());

			// Update next occurrence if startDate and frequency are provided
			if (dto.getFrequency() != null) {
				LocalDate nextOccurrence = nextOccurrenceCalculator.calculateNextOccurrence(
						dto.getStartDate(), dto.getFrequency());
				recurringTransaction.setNextOccurrence(nextOccurrence);
			}
		}

		if (dto.getEndDate() != null) {
			recurringTransaction.setEndDate(dto.getEndDate());
		}
		if (dto.getNextOccurrence() != null) {
			recurringTransaction.setNextOccurrence(dto.getNextOccurrence());
		}
		if (dto.getIsActive() != null) {
			recurringTransaction.setIsActive(dto.getIsActive());
		}

		RecurringTransaction updatedRecurringTransaction = recurringTransactionRepository.save(recurringTransaction);

		return RecurringTransactionMapper.toDTO(updatedRecurringTransaction);
	}

	public PaginatedResponseDTO<RecurringTransactionResponseDTO> searchRecurringTransactions(
			RecurringTransactionFilterDTO filterDTO,
			int page,
			int size,
			String sortBy,
			String direction,
			UUID userId) {
		Sort sort = direction.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Specification<RecurringTransaction> spec = RecurringTransactionSpecification
				.getRecurringTransactionSpecification(filterDTO, userId);

		Page<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAll(spec, pageable);

		List<RecurringTransactionResponseDTO> recurringTransactionResponseDTOs = recurringTransactions
				.getContent().stream()
				.map(RecurringTransactionMapper::toDTO)
				.toList();

		return new PaginatedResponseDTO<>(
				recurringTransactionResponseDTOs,
				recurringTransactions.getNumber(),
				recurringTransactions.getSize(),
				recurringTransactions.getTotalElements(),
				recurringTransactions.getTotalPages(),
				recurringTransactions.isLast(),
				recurringTransactions.isFirst());
	}

	public void updateNextOccurrence(LocalDate nextOccurrence, UUID recurringTransactionId) {
		RecurringTransaction recurringTransaction = recurringTransactionRepository.findById(
				recurringTransactionId)
				.orElseThrow(() -> new RecurringTransactionNotFoundException(
						"Recurring transaction not found. Id: "
								+ recurringTransactionId));

		recurringTransaction.setNextOccurrence(nextOccurrence);

		recurringTransactionRepository.save(recurringTransaction);
	}

	public void deleteRecurringTransaction(UUID recurringTransactionId, UUID userId) {
		recurringTransactionRepository
				.findByRecurringTransactionIdAndUser_UserId(recurringTransactionId, userId)
				.orElseThrow(() -> new RecurringTransactionNotFoundException(
						"Recurring transaction not found. Id: " + recurringTransactionId));

		recurringTransactionRepository.deleteById(recurringTransactionId);
	}
}

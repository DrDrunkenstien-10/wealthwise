package com.wealthwise.transaction.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wealthwise.global.dto.PaginatedResponseDTO;
import com.wealthwise.transaction.dto.TransactionFilterDTO;
import com.wealthwise.transaction.dto.TransactionPatchDTO;
import com.wealthwise.transaction.dto.TransactionRequestDTO;
import com.wealthwise.transaction.dto.TransactionResponseDTO;
import com.wealthwise.transaction.exception.DuplicateTransactionException;
import com.wealthwise.transaction.exception.TransactionNotFoundException;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.mapper.TransactionMapper;
import com.wealthwise.transaction.model.Transaction;
import com.wealthwise.transaction.repository.TransactionRepository;
import com.wealthwise.transaction.specification.TransactionSpecification;
import com.wealthwise.transaction.validator.TransactionValidator;
import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;

@Service
public class TransactionService {
	private final TransactionValidator transactionValidator;
	private final UserRepository userRepository;
	private final TransactionRepository transactionRepository;

	public TransactionService(TransactionValidator transactionValidator,
			UserRepository userRepository,
			TransactionRepository transactionRepository) {
		this.transactionValidator = transactionValidator;
		this.userRepository = userRepository;
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO,
			UUID userId) {
				
		if (transactionRepository.existsByUserIdAndName(userId, transactionRequestDTO.getName())) {
			throw new DuplicateTransactionException("You already have a transaction with this name.");
		}

		transactionValidator.validateTransactionCreationInput(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(
						"User not found. User Id: " + userId));

		Transaction newTransaction = transactionRepository.save(TransactionMapper
				.toModel(transactionRequestDTO, user));

		return TransactionMapper.toDTO(newTransaction);
	}

	public List<TransactionResponseDTO> readTransaction() {
		List<Transaction> transactions = transactionRepository.findAll();

		List<TransactionResponseDTO> transactionResponseDTOs = transactions.stream()
				.map(transaction -> TransactionMapper.toDTO(transaction)).toList();

		return transactionResponseDTOs;
	}

	public TransactionResponseDTO getTransactionByIdAndUserId(UUID transactionId, UUID userId) {
		Transaction transaction = transactionRepository.findByTransactionIdAndUserId(transactionId, userId)
				.orElseThrow(
						() -> new TransactionNotFoundException("Transaction not found or does not belong to user"));

		return TransactionMapper.toDTO(transaction);
	}

	public PaginatedResponseDTO<TransactionResponseDTO> getTransactionsByUserId(
			UUID userId, int page, int size, String sortBy) {

		transactionValidator.validateForReadTransactionByUserId(userId);

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

		Page<Transaction> transactions = transactionRepository.findByUser_UserId(userId, pageable);

		List<TransactionResponseDTO> transactionDTOs = transactions.getContent().stream()
				.map(TransactionMapper::toDTO)
				.toList();

		return new PaginatedResponseDTO<>(
				transactionDTOs,
				transactions.getNumber(),
				transactions.getSize(),
				transactions.getTotalElements(),
				transactions.getTotalPages(),
				transactions.isLast(),
				transactions.isFirst());
	}

	public PaginatedResponseDTO<TransactionResponseDTO> searchTransactions(
			TransactionFilterDTO filter,
			int page,
			int size,
			String sortBy,
			String direction,
			UUID userId) {

		Sort sort = direction.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Specification<Transaction> spec = TransactionSpecification.getTransactionSpecification(filter, userId);

		Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

		List<TransactionResponseDTO> transactionDTOs = transactions.getContent().stream()
				.map(TransactionMapper::toDTO)
				.toList();

		return new PaginatedResponseDTO<>(
				transactionDTOs,
				transactions.getNumber(),
				transactions.getSize(),
				transactions.getTotalElements(),
				transactions.getTotalPages(),
				transactions.isLast(),
				transactions.isFirst());
	}

	public TransactionResponseDTO updateTransaction(UUID transactionId,
			TransactionPatchDTO transactionPatchDTO, UUID userId) {
		Transaction transaction = transactionRepository.findByTransactionIdAndUserId(transactionId, userId)
				.orElseThrow(
						() -> new TransactionNotFoundException("Transaction not found or does not belong to user"));

		if (transactionPatchDTO.getName() != null) {
			transaction.setName(transactionPatchDTO.getName());
		}

		if (transactionPatchDTO.getDescription() != null) {
			transaction.setDescription(transactionPatchDTO.getDescription());
		}

		if (transactionPatchDTO.getAmount() != null) {
			transaction.setAmount(transactionPatchDTO.getAmount());
		}

		if (transactionPatchDTO.getCategory() != null) {
			transaction.setCategory(transactionPatchDTO.getCategory());
		}

		if (transactionPatchDTO.getPaymentType() != null) {
			transaction.setPaymentType(transactionPatchDTO.getPaymentType());
		}

		if (transactionPatchDTO.getDate() != null) {
			transaction.setDate(transactionPatchDTO.getDate());
		}

		if (transactionPatchDTO.getTransactionType() != null) {
			transaction.setTransactionType(transactionPatchDTO.getTransactionType());
		}

		Transaction patchedTransaction = transactionRepository.save(transaction);

		return TransactionMapper.toDTO(patchedTransaction);
	}

	public void deleteTransaction(UUID transactionId, UUID userId) {
		transactionRepository.findByTransactionIdAndUserId(transactionId, userId)
				.orElseThrow(
						() -> new TransactionNotFoundException("Transaction not found or does not belong to user"));

		transactionRepository.deleteById(transactionId);
	}
}

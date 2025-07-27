package com.wealthwise.recurringtransaction.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wealthwise.recurringtransaction.dto.RecurringTransactionResponseDTO;
import com.wealthwise.recurringtransaction.mapper.RecurringTransactionMapper;
import com.wealthwise.recurringtransaction.projection.RecurringTransactionScheduleView;
import com.wealthwise.recurringtransaction.repository.RecurringTransactionRepository;
import com.wealthwise.transaction.service.TransactionService;

@Service
public class TaskSchedulerService {
	private final RecurringTransactionRepository recurringTransactionRepository;
	private final RecurringTransactionService recurringTransactionService;
	private final TransactionService transactionService;
	private final NextOccurrenceCalculator nextOccurrenceCalculator;

	public TaskSchedulerService(RecurringTransactionRepository recurringTransactionRepository,
			RecurringTransactionService recurringTransactionService,
			TransactionService transactionService,
			NextOccurrenceCalculator nextOccurrenceCalculator) {
		this.recurringTransactionRepository = recurringTransactionRepository;
		this.recurringTransactionService = recurringTransactionService;
		this.transactionService = transactionService;
		this.nextOccurrenceCalculator = nextOccurrenceCalculator;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void performScheduledTask() {
		LocalDate today = LocalDate.now();

		List<RecurringTransactionScheduleView> todaysSchedules = recurringTransactionRepository
				.findTodayTransactionSchedules();

		for (RecurringTransactionScheduleView schedule : todaysSchedules) {
			RecurringTransactionResponseDTO transactionDetails = recurringTransactionService
					.readRecurringTransactionByIdAndUserId(schedule.getRecurringTransactionId(),
							schedule.getUserId());

			transactionService.createTransaction(
					RecurringTransactionMapper.toTransactionRequestDTO(transactionDetails,
							transactionDetails.getUserId()),
					transactionDetails.getUserId());

			if (!today.equals(schedule.getEndDate())) {
				LocalDate nextOccurrence = nextOccurrenceCalculator.calculateNextOccurrence(
						schedule.getNextOccurrence(), // More accurate than "today"
						schedule.getFrequency());

				recurringTransactionService.updateNextOccurrence(nextOccurrence,
						schedule.getRecurringTransactionId());
			}
		}
	}
}

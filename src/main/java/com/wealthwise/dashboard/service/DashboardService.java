package com.wealthwise.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wealthwise.dashboard.dto.DashboardSummaryResponseDTO;
import com.wealthwise.dashboard.projection.DashboardSummary;
import com.wealthwise.transaction.enums.TransactionType;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.repository.TransactionRepository;
import com.wealthwise.user.repository.UserRepository;

@Service
public class DashboardService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public DashboardSummaryResponseDTO readMonthlySummaryForUser(YearMonth month, UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<DashboardSummary> incomeSummaryList = transactionRepository
                .findTransactionSummariesByUserAndDateRangeAndType(userId, start, end, TransactionType.INCOME);

        List<DashboardSummary> expenseSummaryList = transactionRepository
                .findTransactionSummariesByUserAndDateRangeAndType(userId, start, end, TransactionType.EXPENSE);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (DashboardSummary incomeSummary : incomeSummaryList) {
            income = income.add(incomeSummary.getAmount());
        }
        for (DashboardSummary expenseSummary : expenseSummaryList) {
            expenses = expenses.add(expenseSummary.getAmount());
        }

        BigDecimal savings = income.subtract(expenses);

        DashboardSummaryResponseDTO dashboardSummaryResponseDTO = new DashboardSummaryResponseDTO();

        dashboardSummaryResponseDTO.setMonth(month);
        dashboardSummaryResponseDTO.setIncome(income);
        dashboardSummaryResponseDTO.setExpenses(expenses);
        dashboardSummaryResponseDTO.setSavings(savings);

        return dashboardSummaryResponseDTO;
    }
}

package com.wealthwise.chart.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wealthwise.chart.dto.piechart.PieChartResponseDTO;
import com.wealthwise.chart.projection.PieChartSummary;
import com.wealthwise.chart.projection.TotalAmount;
import com.wealthwise.transaction.enums.TransactionType;
import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.transaction.repository.TransactionRepository;
import com.wealthwise.user.repository.UserRepository;

@Service
public class ChartService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public ChartService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<PieChartResponseDTO> getMonthlyExpenseSummaryByCategory(UUID userId, YearMonth month) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<PieChartSummary> pieChartSummaryList = transactionRepository
                .findTransactionPieChartByUserAndDateRangeAndType(userId, start, end, TransactionType.EXPENSE);

        TotalAmount totalAmount = transactionRepository
                .findSumOfAmountForPieChartByUserAndDateRangeAndType(userId, start, end, TransactionType.EXPENSE);

        List<PieChartResponseDTO> pieChartResponseDTOList = new ArrayList<>();

        for (PieChartSummary pieChartSummary : pieChartSummaryList) {
            PieChartResponseDTO pieChartResponseDTO = new PieChartResponseDTO();

            pieChartResponseDTO.setAmount(pieChartSummary.getAmount());
            pieChartResponseDTO.setCategory(pieChartSummary.getCategory());

            BigDecimal percentage = pieChartSummary.getAmount()
                    .divide(totalAmount.getAmount(), 4, RoundingMode.HALF_UP) // specify scale and rounding
                    .multiply(new BigDecimal("100"));

            pieChartResponseDTO.setPercentage(percentage);

            pieChartResponseDTOList.add(pieChartResponseDTO);
        }

        return pieChartResponseDTOList;
    }
}

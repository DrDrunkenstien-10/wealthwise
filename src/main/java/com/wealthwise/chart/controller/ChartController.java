package com.wealthwise.chart.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wealthwise.chart.dto.piechart.PieChartResponseDTO;
import com.wealthwise.chart.service.ChartService;
import com.wealthwise.user.service.CurrentUserProvider;

@RestController
@RequestMapping("/api/v1/chart")
public class ChartController {
    private final ChartService chartService;
    private final CurrentUserProvider currentUserProvider;

    public ChartController(ChartService chartService, CurrentUserProvider currentUserProvider) {
        this.chartService = chartService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/expense-summary-by-category")
    public ResponseEntity<List<PieChartResponseDTO>> getMonthlyExpenseSummaryByCategory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("month") YearMonth month) {

        UUID userId = currentUserProvider.getCurrentUserId(jwt);

        List<PieChartResponseDTO> pieChartResponseDTO = chartService.getMonthlyExpenseSummaryByCategory(userId, month);

        return ResponseEntity.ok().body(pieChartResponseDTO);
    }
}

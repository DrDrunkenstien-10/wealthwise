package com.wealthwise.dashboard.controller;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wealthwise.dashboard.dto.DashboardSummaryResponseDTO;
import com.wealthwise.dashboard.service.DashboardService;
import com.wealthwise.user.service.CurrentUserProvider;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final CurrentUserProvider currentUserProvider;

    public DashboardController(DashboardService dashboardService, CurrentUserProvider currentUserProvider) {
        this.dashboardService = dashboardService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/transaction-summary")
    public ResponseEntity<DashboardSummaryResponseDTO> readUserMonthlyDashboard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("month") YearMonth month) {

        UUID userId = currentUserProvider.getCurrentUserId(jwt);

        DashboardSummaryResponseDTO dashboardSummaryResponseDTO = dashboardService
                .readMonthlySummaryForUser(month, userId);

        return ResponseEntity.ok().body(dashboardSummaryResponseDTO);
    }
}

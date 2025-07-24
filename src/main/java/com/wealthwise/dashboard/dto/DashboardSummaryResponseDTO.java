package com.wealthwise.dashboard.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public class DashboardSummaryResponseDTO {
    private YearMonth month;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal savings;

    // Getters and setters
    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }
}
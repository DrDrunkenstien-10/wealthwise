package com.wealthwise.chart.dto.piechart;

import java.math.BigDecimal;

public class PieChartResponseDTO {
    private String category;
    private BigDecimal amount;
    private BigDecimal percentage;
    
    // Getters and setters
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getPercentage() {
        return percentage;
    }
    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}

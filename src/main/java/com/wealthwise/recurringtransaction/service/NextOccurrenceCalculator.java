package com.wealthwise.recurringtransaction.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.wealthwise.recurringtransaction.enums.Frequency;

@Service
public class NextOccurrenceCalculator {
    public LocalDate calculateNextOccurrence(LocalDate startDate, Frequency frequency) {
        if (startDate == null || frequency == null) {
            throw new IllegalArgumentException("Start date and frequency must not be null");
        }

        LocalDate today = LocalDate.now();

        // If the start date is in the future, the next occurrence is the start date
        // itself
        LocalDate next = startDate.isAfter(today) ? startDate : today;

        switch (frequency) {
            case DAILY:
                return next.plusDays(1);
            case WEEKLY:
                return next.plusWeeks(1);
            case MONTHLY:
                return next.plusMonths(1);
            case YEARLY:
                return next.plusYears(1);
            default:
                throw new UnsupportedOperationException("Unsupported frequency: " + frequency);
        }
    }
}

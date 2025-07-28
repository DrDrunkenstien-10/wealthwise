package com.wealthwise.recurringtransaction.service;

import com.wealthwise.recurringtransaction.enums.Frequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NextOccurrenceCalculatorTest {

    private NextOccurrenceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new NextOccurrenceCalculator();
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNextOccurrence(null, Frequency.DAILY));
        assertEquals("Start date and frequency must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFrequencyIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNextOccurrence(LocalDate.now(), null));
        assertEquals("Start date and frequency must not be null", ex.getMessage());
    }

    @Test
    void shouldReturnStartDatePlusOneDayWhenStartDateIsFutureAndFrequencyIsDaily() {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate result = calculator.calculateNextOccurrence(startDate, Frequency.DAILY);

        assertEquals(startDate.plusDays(1), result);
    }

    @Test
    void shouldReturnTodayPlusOneMonthWhenStartDateIsPastAndFrequencyIsMonthly() {
        LocalDate pastDate = LocalDate.now().minusDays(5);
        LocalDate expected = LocalDate.now().plusMonths(1);

        LocalDate result = calculator.calculateNextOccurrence(pastDate, Frequency.MONTHLY);

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnCorrectDateForAllFrequencies() {
        LocalDate today = LocalDate.now();

        assertEquals(today.plusDays(1), calculator.calculateNextOccurrence(today.minusDays(1), Frequency.DAILY));
        assertEquals(today.plusWeeks(1), calculator.calculateNextOccurrence(today.minusDays(1), Frequency.WEEKLY));
        assertEquals(today.plusMonths(1), calculator.calculateNextOccurrence(today.minusDays(1), Frequency.MONTHLY));
        assertEquals(today.plusYears(1), calculator.calculateNextOccurrence(today.minusDays(1), Frequency.YEARLY));
    }
}

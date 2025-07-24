package com.wealthwise.recurringtransaction.projection;

import java.time.LocalDate;
import java.util.UUID;

import com.wealthwise.recurringtransaction.enums.Frequency;

public interface RecurringTransactionScheduleView {
    UUID getRecurringTransactionId();
    
    UUID getUserId();
    
    LocalDate getNextOccurrence();

    Frequency getFrequency();   

    LocalDate getEndDate();
}

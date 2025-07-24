package com.wealthwise.recurringtransaction.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wealthwise.recurringtransaction.model.RecurringTransaction;
import com.wealthwise.recurringtransaction.projection.RecurringTransactionScheduleView;

@Repository
public interface RecurringTransactionRepository
        extends JpaRepository<RecurringTransaction, UUID>, JpaSpecificationExecutor<RecurringTransaction> {
    Page<RecurringTransaction> findByUser_UserId(UUID userId, Pageable pageable);

    @Query("""
                SELECT
                    r.recurringTransactionId AS recurringTransactionId,
                    r.user.userId AS userId,
                    r.nextOccurrence AS nextOccurrence,
                    r.frequency AS frequency,
                    r.endDate AS endDate
                FROM RecurringTransaction r
                WHERE r.nextOccurrence = CURRENT_DATE
            """)
    List<RecurringTransactionScheduleView> findTodayTransactionSchedules();

    Optional<RecurringTransaction> findByRecurringTransactionIdAndUser_UserId(UUID recurringTransactionId, UUID userId);

    @Query("SELECT COUNT(r) > 0 FROM RecurringTransaction r WHERE r.user.userId = :userId AND LOWER(r.recurringTransactionName) = LOWER(:recurringTransactionName)")
    boolean existsByUserIdAndRecurringTransactionName(@Param("userId") UUID userId,
            @Param("recurringTransactionName") String recurringTransactionName);
}

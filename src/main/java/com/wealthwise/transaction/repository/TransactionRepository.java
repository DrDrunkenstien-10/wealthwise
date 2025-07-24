package com.wealthwise.transaction.repository;

import java.time.LocalDateTime;
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

import com.wealthwise.chart.projection.PieChartSummary;
import com.wealthwise.chart.projection.TotalAmount;
import com.wealthwise.dashboard.projection.DashboardSummary;
import com.wealthwise.transaction.enums.TransactionType;
import com.wealthwise.transaction.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
	@Query("SELECT t FROM Transaction t WHERE t.transactionId = :transactionId AND t.user.userId = :userId")
	Optional<Transaction> findByTransactionIdAndUserId(@Param("transactionId") UUID transactionId,
			@Param("userId") UUID userId);

	Page<Transaction> findByUser_UserId(UUID userId, Pageable pageable);

	@Query("""
			    SELECT t.amount AS amount, t.transactionType AS transactionType
			    FROM Transaction t
			    WHERE t.user.userId = :userId
			    AND t.date BETWEEN :start AND :end
			    AND t.transactionType = :type
			""")
	List<DashboardSummary> findTransactionSummariesByUserAndDateRangeAndType(
			@Param("userId") UUID userId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("type") TransactionType type);

	@Query("""
			    SELECT t.amount AS amount, t.category AS category
			    FROM Transaction t
			    WHERE t.user.userId = :userId
			    AND t.date BETWEEN :start AND :end
			    AND t.transactionType = :type
			""")
	List<PieChartSummary> findTransactionPieChartByUserAndDateRangeAndType(
			@Param("userId") UUID userId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("type") TransactionType type);

	@Query("""
				SELECT SUM(t.amount) AS amount
				FROM Transaction t
				WHERE t.user.userId = :userId
				AND t.date BETWEEN :start AND :end
				AND t.transactionType = :type
			""")
	TotalAmount findSumOfAmountForPieChartByUserAndDateRangeAndType(
			@Param("userId") UUID userId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("type") TransactionType type);

	@Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.user.userId = :userId AND LOWER(t.name) = LOWER(:name)")
	boolean existsByUserIdAndName(@Param("userId") UUID userId, @Param("name") String name);
}

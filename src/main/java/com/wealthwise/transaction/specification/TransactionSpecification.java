package com.wealthwise.transaction.specification;

import org.springframework.data.jpa.domain.Specification;

import com.wealthwise.transaction.dto.TransactionFilterDTO;
import com.wealthwise.transaction.model.Transaction;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> getTransactionSpecification(TransactionFilterDTO filter, UUID userId) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (userId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("user").get("userId"), userId));
            }

            if (filter.getTransactionName() != null && !filter.getTransactionName().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + filter.getTransactionName().toLowerCase() + "%"));
            }

            if (filter.getMinAmount() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
            }

            if (filter.getMaxAmount() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("amount"), filter.getMaxAmount()));
            }

            if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(root.get("category")), filter.getCategory().toLowerCase()));
            }

            if (filter.getPaymentType() != null && !filter.getPaymentType().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(root.get("paymentType")), filter.getPaymentType().toLowerCase()));
            }

            if (filter.getTransactionType() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("transactionType"), filter.getTransactionType()));
            }

            if (filter.getDate() != null) {
                // Filter by same day — ignoring time
                LocalDateTime startOfDay = filter.getDate().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1);

                predicate = cb.and(predicate,
                        cb.between(root.get("date"), startOfDay, endOfDay));
            }

            return predicate;
        };
    }
}

package com.wealthwise.recurringtransaction.specification;

import org.springframework.data.jpa.domain.Specification;

import com.wealthwise.recurringtransaction.dto.RecurringTransactionFilterDTO;
import com.wealthwise.recurringtransaction.model.RecurringTransaction;

import jakarta.persistence.criteria.Predicate;

import java.util.UUID;

public class RecurringTransactionSpecification {

    public static Specification<RecurringTransaction> getRecurringTransactionSpecification(
            RecurringTransactionFilterDTO filter, UUID userId) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (userId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("user").get("userId"), userId));
            }

            if (filter.getRecurringTransactionName() != null && !filter.getRecurringTransactionName().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("recurringTransactionName")),
                                "%" + filter.getRecurringTransactionName().toLowerCase() + "%"));
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

            if (filter.isActive() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("isActive"), filter.isActive()));
            }

            if (filter.getFrequency() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("frequency"), filter.getFrequency()));
            }

            return predicate;
        };
    }
}

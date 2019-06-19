package com.regisan.payments.api.repository;

import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.domain.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    static Specification<Transaction> hasNegativeBalance() {
        return (transaction, cQuery, cBuilder) ->
                cBuilder.lessThan(transaction.get("balance"), BigDecimal.ZERO);
    }

    static Specification<Transaction> hasOperationType(OperationType op) {
        return (transaction, cQuery, cBuilder) ->
                cBuilder.equal(transaction.get("operationType"), op);
    }

}

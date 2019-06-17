package com.regisan.payments.api.repository;

import com.regisan.payments.api.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

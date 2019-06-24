package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TransactionStrategy {

    List<Transaction> process(TransactionDTO dto);
}

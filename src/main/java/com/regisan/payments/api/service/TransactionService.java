package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionFactory factory;

    public List<List<Transaction>> add(List<TransactionDTO> transactions) {
        List<List<Transaction>> result = new ArrayList<>();

        for (TransactionDTO dto : transactions)
            result.add(this.add(dto));

        return result;
    }

    public List<Transaction> add(TransactionDTO dto) {
        TransactionStrategy strategy = factory.getStrategy(dto.getOperationType());
        List<Transaction> t = strategy.process(dto);
        return repository.saveAll(t);
    }
}

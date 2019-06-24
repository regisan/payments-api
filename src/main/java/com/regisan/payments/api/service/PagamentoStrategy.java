package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.repository.TransactionRepository;
import com.regisan.payments.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.regisan.payments.api.domain.OperationType.PAGAMENTO;
import static com.regisan.payments.api.domain.OperationType.SAQUE;
import static com.regisan.payments.api.repository.TransactionRepository.hasNegativeBalance;
import static com.regisan.payments.api.repository.TransactionRepository.hasOperationType;
import static com.regisan.payments.api.repository.TransactionRepository.hasAccount;
import static org.springframework.data.jpa.domain.Specification.not;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class PagamentoStrategy implements TransactionStrategy {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository repository;

    @Override
    public List<Transaction> process(TransactionDTO dto) {
        BigDecimal transactionAmount = dto.getAmount();
        Account account = accountService.findById(dto.getAccountId());

        Transaction t = new Transaction();
        t.setOperationType(dto.getOperationType());
        t.setAmount(transactionAmount);

        // busca por transacoes passadas com balance menor que zero
        List<Transaction> registers = repository.findAll(
                where(hasAccount(account)
                        .and(not(hasOperationType(PAGAMENTO))
                                .and(hasNegativeBalance()))));

        // se nao houver transacoes, acrescenta o valor do pagamento ao limite de credito
        if (registers.isEmpty()) {
            accountService.updateLimits(account.getId(), transactionAmount, BigDecimal.ZERO);
        }
        else {
            Collections.sort(registers);
            BigDecimal credit = BigDecimal.ZERO;
            BigDecimal withdrawal = BigDecimal.ZERO;

            for (Transaction register : registers) {
                // despesa menor que o pagamento
                if (register.getBalance().abs().compareTo(transactionAmount) < 1) {
                    if (register.getOperationType().equals(SAQUE))
                        withdrawal = withdrawal.add(register.getBalance().abs());
                    else
                        credit = credit.add(register.getBalance().abs());

                    transactionAmount = transactionAmount.add(register.getBalance());
                    register.setBalance(BigDecimal.ZERO);
                }
                // despesa maior que o pagamento
                else {
                    if (register.getOperationType().equals(SAQUE))
                        withdrawal = withdrawal.add(transactionAmount);
                    else
                        credit = credit.add(transactionAmount);

                    register.setBalance(register.getBalance().add(transactionAmount));
                    transactionAmount = BigDecimal.ZERO;
                }

                repository.save(register);

            }

            accountService.updateLimits(account.getId(), credit, BigDecimal.ZERO);
            accountService.updateLimits(account.getId(), BigDecimal.ZERO, withdrawal);
        }

        t.setBalance(transactionAmount);
        t.setAccount(account);
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));
        t.setOperationType(dto.getOperationType());

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(t);

        return transactionList;
    }
}

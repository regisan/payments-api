package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.exception.TransactionException;
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
import static com.regisan.payments.api.repository.TransactionRepository.hasPositiveBalance;
import static com.regisan.payments.api.repository.TransactionRepository.hasOperationType;
import static com.regisan.payments.api.repository.TransactionRepository.hasAccount;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class CompraAVistaStrategy implements TransactionStrategy {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository repository;

    @Override
    public List<Transaction> process(TransactionDTO dto) {

        BigDecimal transactionAmount = dto.getAmount();
        Account account = accountService.findById(dto.getAccountId());

        if (account.getAvailableCreditLimit().compareTo(transactionAmount) < 0)
            throw new TransactionException("Sem limite para efetuar a transacao.");

        Transaction t = new Transaction();
        t.setAccount(account);
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));
        t.setOperationType(dto.getOperationType());
        t.setAmount(transactionAmount.negate());

        accountService.updateLimits(account.getId(), transactionAmount.negate(), BigDecimal.ZERO);

        // busca por pagamentos passados com balance maior que zero
        List<Transaction> payments = repository.findAll(
                where(hasAccount(account)
                        .and(hasOperationType(PAGAMENTO).and(hasPositiveBalance()))));

        if (!payments.isEmpty()) {
            Collections.sort(payments);

            for (Transaction payment : payments) {
                BigDecimal balance = payment.getBalance();

                if (balance.compareTo(transactionAmount) >= 0) {
                    payment.setBalance(balance.subtract(transactionAmount));
                    transactionAmount = BigDecimal.ZERO;
                }
                else {
                    transactionAmount = transactionAmount.subtract(balance);
                    payment.setBalance(BigDecimal.ZERO);
                }

                repository.save(payment);

                if (transactionAmount.compareTo(BigDecimal.ZERO) == 0)
                    break;
            }
        }

        t.setBalance(transactionAmount.negate());

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(t);

        return transactionList;
    }
}

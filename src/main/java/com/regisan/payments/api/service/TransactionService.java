package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.exception.TransactionException;
import com.regisan.payments.api.repository.TransactionRepository;
import com.regisan.payments.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.regisan.payments.api.domain.OperationType.*;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    public Transaction add(TransactionDTO dto) {

        Account account = accountService.findById(dto.getAccountId());
        OperationType operationType = dto.getOperationType();
        BigDecimal transactionAmount = dto.getAmount();

        if ((operationType.equals(COMPRA_A_VISTA) || operationType.equals(COMPRA_PARCELADA)) &&
                account.hasCreditLimit(transactionAmount)) {
            accountService.updateLimits(account.getId(), transactionAmount.negate(), BigDecimal.ZERO);
        }
        else if (operationType.equals(SAQUE) && account.hasWithdrawalLimit(transactionAmount)) {
            accountService.updateLimits(account.getId(), BigDecimal.ZERO, transactionAmount.negate());
        }
        else
            throw new TransactionException("Sem limite disponivel para efetuar a transacao");

        Transaction t = new Transaction();
        t.setAccount(account);
        t.setOperationType(operationType);
        t.setAmount(transactionAmount.negate());

        // verificar se tem saldo
        t.setBalance(transactionAmount.negate());
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));

        return repository.save(t);
    }
}

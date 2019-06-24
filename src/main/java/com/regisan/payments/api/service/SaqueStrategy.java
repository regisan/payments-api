package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.exception.TransactionException;
import com.regisan.payments.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SaqueStrategy implements TransactionStrategy {

    @Autowired
    private AccountService accountService;

    @Override
    public List<Transaction> process(TransactionDTO dto) {
        BigDecimal transactionAmount = dto.getAmount();
        Account account = accountService.findById(dto.getAccountId());

        if (account.getAvailableWithdrawalLimit().compareTo(transactionAmount) < 0)
            throw new TransactionException("Sem limite para efetuar a transacao.");

        accountService.updateLimits(account.getId(), BigDecimal.ZERO, transactionAmount.negate());

        Transaction t = new Transaction();
        t.setAccount(account);
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));
        t.setOperationType(dto.getOperationType());
        t.setAmount(transactionAmount.negate());
        t.setBalance(transactionAmount.negate());

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(t);

        return transactionList;
    }
}

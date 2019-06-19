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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.regisan.payments.api.repository.TransactionRepository.*;
import static org.springframework.data.jpa.domain.Specification.*;
import static com.regisan.payments.api.domain.OperationType.*;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    public void add(List<TransactionDTO> transactions) {
        for (TransactionDTO dto : transactions)
            this.add(dto);
    }

    //TODO: Refatorar
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
        else if (!operationType.equals(PAGAMENTO)) {
            throw new TransactionException("Sem limite disponivel para efetuar a transacao");
        }


        Transaction t = new Transaction();
        t.setAccount(account);
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));

        if (operationType.equals(PAGAMENTO)) {
            t.setOperationType(operationType);
            t.setAmount(transactionAmount);

            // busca por transacoes passadas com balance menor que zero
            List<Transaction> registers = repository.findAll(not(hasOperationType(PAGAMENTO)).and(hasNegativeBalance()));
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

            t.setBalance(transactionAmount);
        }
        else {
            t.setOperationType(operationType);
            t.setAmount(transactionAmount.negate());
            t.setBalance(transactionAmount.negate());
        }

        return repository.save(t);
    }
}

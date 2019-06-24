package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.exception.TransactionException;
import com.regisan.payments.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CompraParceladaStrategy implements TransactionStrategy {

    @Autowired
    private AccountService accountService;

    @Override
    public List<Transaction> process(TransactionDTO dto) {
        BigDecimal transactionAmount = dto.getAmount();
        Account account = accountService.findById(dto.getAccountId());

        if (account.getAvailableCreditLimit().compareTo(transactionAmount) < 0)
            throw new TransactionException("Sem limite para efetuar a transacao.");

        accountService.updateLimits(account.getId(), transactionAmount.negate(), BigDecimal.ZERO);

        // verifica se o installment_plan foi informado para este tipo de operacao
        if (dto.getInstallmentPlan() == null)
            throw new TransactionException("Informar installment_plan para tipo de operacao " + OperationType.COMPRA_PARCELADA);

        int parcelas = dto.getInstallmentPlan();

        // divide o total pelo numero de parcelas e gera uma transacao para cada parcela.
        BigDecimal valorParcela = transactionAmount.divide(new BigDecimal(parcelas), 2, RoundingMode.HALF_UP);

        List<Transaction> transactionList = new ArrayList<>(parcelas);

        for (int i = 0; i < parcelas; i++) {

            Transaction t = new Transaction();
            t.setAccount(account);
            t.setEventDate(new Date());

            Date dueDate = DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE);
            t.setDueDate(DateUtil.addMonth(dueDate, i));

            t.setOperationType(dto.getOperationType());

            if (i < parcelas - 1) {
                t.setAmount(valorParcela.negate());
                t.setBalance(valorParcela.negate());
            }
            else {
                // evita problema de arredondamento. ex: 1000/3 = 333.33
                t.setAmount(transactionAmount.negate());
                t.setBalance(transactionAmount.negate());
            }

            transactionAmount = transactionAmount.subtract(valorParcela);

            transactionList.add(t);
        }

        return transactionList;
    }
}

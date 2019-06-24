package com.regisan.payments.api.controller;

import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.PaymentDTO;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PaymentsController {

    private TransactionService service;

    public PaymentsController(TransactionService service) {
        this.service = service;
    }

    @ApiOperation(value = "It adds a new payment.")
    @PostMapping("v1/payments")
    public ResponseEntity<List<List<Transaction>>> addPayments(@RequestBody List<PaymentDTO> payments) {

        List<TransactionDTO> transactions = new ArrayList<>(payments.size());

        for (PaymentDTO payment : payments) {
            TransactionDTO t = new TransactionDTO();
            t.setAccountId(payment.getAccountId());
            t.setOperationTypeId(OperationType.PAGAMENTO.getId());
            t.setAmount(payment.getAmount());
            transactions.add(t);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(transactions));
    }
}

package com.regisan.payments.api.controller;

import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.dto.PaymentDTO;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private TransactionService service;


    public PaymentsController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addPayments(@RequestBody List<PaymentDTO> payments) {

        List<TransactionDTO> transactions = new ArrayList<>(payments.size());

        for (PaymentDTO payment : payments) {
            TransactionDTO t = new TransactionDTO();
            t.setAccountId(payment.getAccountId());
            t.setOperationTypeId(OperationType.PAGAMENTO.getId());
            t.setAmount(payment.getAmount());
            transactions.add(t);
        }

        service.add(transactions);

        return ResponseEntity.ok().build();
    }
}

package com.regisan.payments.api.controller;

import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ApiOperation(value = "It adds a new transaction.")
    @PostMapping("v1/transactions")
    public ResponseEntity<List<Transaction>> addTransaction(@RequestBody TransactionDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.add(dto));
    }
}

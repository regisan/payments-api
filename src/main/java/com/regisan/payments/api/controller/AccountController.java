package com.regisan.payments.api.controller;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.exception.AccountException;
import com.regisan.payments.api.service.AccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation(value = "Add a new account.")
    @PostMapping("v1/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.add(account));
    }

    @ApiOperation(value = "It updates the credit and withdrawal limits of an account.")
    @PatchMapping("v1/accounts/{id}")
    public ResponseEntity<Account> updateAccountLimits(@PathVariable("id") Long accountId,
                                                       @RequestBody Map<String, Map<String, BigDecimal>> limitsUpdate) {

        if (limitsUpdate.get("available_credit_limit") == null ||
                limitsUpdate.get("available_withdrawal_limit") == null ||
                limitsUpdate.get("available_credit_limit").get("amount") == null ||
                limitsUpdate.get("available_withdrawal_limit").get("amount") == null) {

            throw new AccountException("Payload request invalido");
        }

        BigDecimal creditAmount = limitsUpdate.get("available_credit_limit").get("amount");
        BigDecimal withdrawalAmount = limitsUpdate.get("available_withdrawal_limit").get("amount");

        return ResponseEntity
                .ok()
                .body(accountService.updateLimits(accountId, creditAmount, withdrawalAmount));
    }

    @ApiOperation(value = "It returns the credit and withdrawal limits of all accounts.")
    @GetMapping("v1/accounts/limits")
    public List<Account> getAccountLimits() {
        return accountService.findAll();
    }
}

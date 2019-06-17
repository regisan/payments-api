package com.regisan.payments.api.controller;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.exception.AccountException;
import com.regisan.payments.api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.add(account));
    }

    @PatchMapping("/{id}")
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

    @GetMapping("/limits")
    public List<Account> getAccountLimits() {
        return accountService.findAll();
    }
}

package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.exception.AccountException;
import com.regisan.payments.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account add(Account newAccount) {
        Optional<Account> opt= repository.findById(newAccount.getId());

        if (opt.isPresent())
            throw new AccountException("Conta ja existe");

        return repository.save(newAccount);
    }

    public Account updateLimits(Long accountId, BigDecimal creditAmount, BigDecimal withdrawalAmount) {

        Account account = this.findById(accountId);

        BigDecimal newCreditLimit = account.getAvailableCreditLimit().add(creditAmount);
        BigDecimal newWithdrawalLimit = account.getAvailableWithdrawalLimit().add(withdrawalAmount);

        if (newCreditLimit.compareTo(BigDecimal.ZERO) > 0)
            account.setAvailableCreditLimit(newCreditLimit);
        else
            account.setAvailableCreditLimit(BigDecimal.ZERO);

        if (newWithdrawalLimit.compareTo(BigDecimal.ZERO) > 0)
            account.setAvailableWithdrawalLimit(newWithdrawalLimit);
        else
            account.setAvailableWithdrawalLimit(BigDecimal.ZERO);

        return repository.save(account);
    }

    public Account findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new AccountException("Conta inexistente"));
    }

    public List<Account> findAll() {
        return repository.findAll();
    }
}

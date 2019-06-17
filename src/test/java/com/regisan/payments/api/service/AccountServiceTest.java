package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.exception.AccountException;
import com.regisan.payments.api.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService service;

    @MockBean
    private AccountRepository repository;

    private Account account;

    @Before
    public void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAvailableCreditLimit(new BigDecimal("5000.00"));
        account.setAvailableWithdrawalLimit(new BigDecimal("5000.00"));
    }

    @Test
    public void testAddAccount() {

        given(repository.save(account)).willReturn(account);

        Account response = service.add(account);
        assertThat(response.getAvailableCreditLimit(), is(account.getAvailableCreditLimit()));
    }

    @Test(expected = AccountException.class)
    public void testAddDuplicateAccount() {
        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        service.add(account);
    }

    @Test(expected = AccountException.class)
    public void testAccountNotFound() {
        given(repository.findById(account.getId())).willThrow(new AccountException("Conta inexistente"));
        service.findById(account.getId());
    }

    @Test
    public void testUpdateLimitsPositive() {
        BigDecimal amount = new BigDecimal("123.45");
        Account accountWithNewLimits = new Account();
        accountWithNewLimits.setId(account.getId());
        accountWithNewLimits.setAvailableCreditLimit(account.getAvailableCreditLimit().add(amount));
        accountWithNewLimits.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(amount));

        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        given(repository.save(account)).willReturn(accountWithNewLimits);

        Account response = service.updateLimits(account.getId(), amount, amount);

        assertThat(response.getAvailableCreditLimit(), is(accountWithNewLimits.getAvailableCreditLimit()));
        assertThat(response.getAvailableWithdrawalLimit(), is(accountWithNewLimits.getAvailableWithdrawalLimit()));
    }

    @Test
    public void testUpdateLimitsNegative() {
        BigDecimal amount = new BigDecimal("-123.45");
        Account accountWithNewLimits = new Account();
        accountWithNewLimits.setId(account.getId());
        accountWithNewLimits.setAvailableCreditLimit(account.getAvailableCreditLimit().add(amount));
        accountWithNewLimits.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(amount));

        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        given(repository.save(account)).willReturn(accountWithNewLimits);

        Account response = service.updateLimits(account.getId(), amount, amount);

        assertThat(response.getAvailableCreditLimit(), is(accountWithNewLimits.getAvailableCreditLimit()));
        assertThat(response.getAvailableWithdrawalLimit(), is(accountWithNewLimits.getAvailableWithdrawalLimit()));
    }

    @Test
    public void testUpdateLimitsLessThanZero() {
        BigDecimal amount = new BigDecimal("-5123.45");
        Account accountWithNewLimits = new Account();
        accountWithNewLimits.setId(account.getId());
        accountWithNewLimits.setAvailableCreditLimit(BigDecimal.ZERO);
        accountWithNewLimits.setAvailableWithdrawalLimit(BigDecimal.ZERO);

        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        given(repository.save(account)).willReturn(accountWithNewLimits);

        Account response = service.updateLimits(account.getId(), amount, amount);

        assertThat(response.getAvailableCreditLimit(), is(accountWithNewLimits.getAvailableCreditLimit()));
        assertThat(response.getAvailableWithdrawalLimit(), is(accountWithNewLimits.getAvailableWithdrawalLimit()));
    }

    @Test
    public void testUpdateCreditLimitOnly() {
        BigDecimal amount = new BigDecimal("123.45");
        Account accountWithNewLimits = new Account();
        accountWithNewLimits.setId(account.getId());
        accountWithNewLimits.setAvailableCreditLimit(account.getAvailableCreditLimit().add(amount));
        accountWithNewLimits.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(BigDecimal.ZERO));

        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        given(repository.save(account)).willReturn(accountWithNewLimits);

        Account response = service.updateLimits(account.getId(), amount, BigDecimal.ZERO);

        assertThat(response.getAvailableCreditLimit(), is(accountWithNewLimits.getAvailableCreditLimit()));
        assertThat(response.getAvailableWithdrawalLimit(), is(account.getAvailableWithdrawalLimit()));
    }

    @Test
    public void testUpdateWithdrawalLimitOnly() {
        BigDecimal amount = new BigDecimal("123.45");
        Account accountWithNewLimits = new Account();
        accountWithNewLimits.setId(account.getId());
        accountWithNewLimits.setAvailableCreditLimit(account.getAvailableCreditLimit().add(BigDecimal.ZERO));
        accountWithNewLimits.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(amount));

        given(repository.findById(account.getId())).willReturn(Optional.of(account));
        given(repository.save(account)).willReturn(accountWithNewLimits);

        Account response = service.updateLimits(account.getId(), BigDecimal.ZERO, amount);

        assertThat(response.getAvailableCreditLimit(), is(account.getAvailableCreditLimit()));
        assertThat(response.getAvailableWithdrawalLimit(), is(accountWithNewLimits.getAvailableWithdrawalLimit()));
    }

}

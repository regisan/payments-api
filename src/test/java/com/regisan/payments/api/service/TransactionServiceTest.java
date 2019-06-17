package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.domain.Transaction;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.exception.TransactionException;
import com.regisan.payments.api.repository.TransactionRepository;
import com.regisan.payments.api.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService service;

    @MockBean
    private TransactionRepository repository;

    @MockBean
    private AccountService accountService;

    private Account account;

    private TransactionDTO dto;

    @Before
    public void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAvailableCreditLimit(new BigDecimal("5000.00"));
        account.setAvailableWithdrawalLimit(new BigDecimal("5000.00"));

        dto = new TransactionDTO();
        dto.setAccountId(account.getId());
    }

    @Test
    public void testAddTransactionCompraAVista() {
        BigDecimal amount = new BigDecimal("123.45");

        dto.setAmount(amount);
        dto.setOperationTypeId(OperationType.COMPRA_A_VISTA.getId());

        Account updatedAccount = new Account();
        updatedAccount.setId(account.getId());
        updatedAccount.setAvailableCreditLimit(account.getAvailableCreditLimit().subtract(amount));
        updatedAccount.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit());

        given(accountService.findById(account.getId())).willReturn(account);
        given(accountService.updateLimits(account.getId(), amount.negate(), BigDecimal.ZERO)).willReturn(updatedAccount);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setAccount(updatedAccount);
        t.setOperationType(OperationType.COMPRA_A_VISTA);
        t.setAmount(amount.negate());
        t.setBalance(amount.negate());
        t.setEventDate(new Date());
        t.setDueDate(DateUtil.getNextDueDate(t.getEventDate(), Account.DUE_DATE, Transaction.DAYS_BEFORE_DUE_DATE));

        when(repository.save(any(Transaction.class))).thenReturn(t);

        Transaction response = service.add(dto);

        assertNotNull(response);
        assertThat(response.getAccount().getAvailableCreditLimit(), is(account.getAvailableCreditLimit().subtract(amount)));
    }

    @Test(expected = TransactionException.class)
    public void testNoLimitForCompraAVista() {
        BigDecimal amount = new BigDecimal("5123.45");

        dto.setAmount(amount);
        dto.setOperationTypeId(OperationType.COMPRA_A_VISTA.getId());

        given(accountService.findById(account.getId())).willReturn(account);

        service.add(dto);

    }

    @Test(expected = TransactionException.class)
    public void testNoLimitForCompraParcelada() {
        BigDecimal amount = new BigDecimal("5123.45");

        dto.setAmount(amount);
        dto.setOperationTypeId(OperationType.COMPRA_PARCELADA.getId());

        given(accountService.findById(account.getId())).willReturn(account);

        service.add(dto);

    }

    @Test(expected = TransactionException.class)
    public void testNoLimitForSaque() {
        BigDecimal amount = new BigDecimal("5123.45");

        dto.setAmount(amount);
        dto.setOperationTypeId(OperationType.SAQUE.getId());

        given(accountService.findById(account.getId())).willReturn(account);

        service.add(dto);

    }

}

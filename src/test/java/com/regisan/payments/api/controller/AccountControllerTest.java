package com.regisan.payments.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.regisan.payments.api.domain.Account;
import com.regisan.payments.api.exception.AccountException;
import com.regisan.payments.api.repository.AccountRepository;
import com.regisan.payments.api.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountRepository repository;

    @MockBean
    private AccountService service;

    private Account account;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        account = new Account();
        account.setId(1L);
        account.setAvailableCreditLimit(new BigDecimal(5000d));
        account.setAvailableWithdrawalLimit(new BigDecimal(5000d));

    }

    @Test
    public void testAddAccount() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(post("/v1/accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(account)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddDuplicateAccount() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(service.add(account)).willThrow(new AccountException("Conta ja existe"));

        mvc.perform(post("/v1/accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(account)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccounts() throws Exception {
        List<Account> allAccounts = Arrays.asList(account);

        given(service.findAll()).willReturn(allAccounts);
        mvc.perform(get("/v1/accounts/limits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].available_credit_limit",
                        hasToString(account.getAvailableCreditLimit().toString())));
    }

    @Test
    public void testUpdateAccountLimits() throws Exception {
        BigDecimal amount = new BigDecimal("123.45");
        account.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(amount));
        account.setAvailableCreditLimit(account.getAvailableCreditLimit().add(amount));

        Map<String, Map<String, BigDecimal>> limitsUpdate = new LinkedHashMap<>();
        Map<String, BigDecimal> limitAmountCredit = new LinkedHashMap<>();
        limitAmountCredit.put("amount", amount);
        limitsUpdate.put("available_credit_limit", limitAmountCredit);

        Map<String, BigDecimal> limitAmountWithdrawal = new LinkedHashMap<>();
        limitAmountWithdrawal.put("amount", amount);
        limitsUpdate.put("available_withdrawal_limit", limitAmountWithdrawal);

        ObjectMapper mapper = new ObjectMapper();

        given(service.updateLimits(account.getId(), amount, amount)).willReturn(account);

        mvc.perform(patch("/v1/accounts/1")
                .content(mapper.writeValueAsString(limitsUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("available_withdrawal_limit")))
                .andExpect(content().string(containsString("5123.45")));
    }

    @Test
    public void testUpdateAccountLimitsNegative() throws Exception {
        BigDecimal amount = new BigDecimal("-123.45");
        account.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().add(amount));
        account.setAvailableCreditLimit(account.getAvailableCreditLimit().add(amount));

        Map<String, Map<String, BigDecimal>> limitsUpdate = new LinkedHashMap<>();
        Map<String, BigDecimal> limitAmountCredit = new LinkedHashMap<>();
        limitAmountCredit.put("amount", amount);
        limitsUpdate.put("available_credit_limit", limitAmountCredit);

        Map<String, BigDecimal> limitAmountWithdrawal = new LinkedHashMap<>();
        limitAmountWithdrawal.put("amount", amount);
        limitsUpdate.put("available_withdrawal_limit", limitAmountWithdrawal);

        ObjectMapper mapper = new ObjectMapper();

        given(service.updateLimits(account.getId(), amount, amount)).willReturn(account);

        mvc.perform(patch("/v1/accounts/1")
                .content(mapper.writeValueAsString(limitsUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("available_withdrawal_limit")))
                .andExpect(content().string(containsString("available_credit_limit")))
                .andExpect(content().string(containsString("4876.55")));
    }

    @Test
    public void testUpdateAccountLimitsInvalid() throws Exception {
        BigDecimal amount = new BigDecimal("123.45");

        Map<String, Map<String, BigDecimal>> limitsUpdate = new LinkedHashMap<>();
        Map<String, BigDecimal> limitAmountCredit = new LinkedHashMap<>();
        limitAmountCredit.put("amount", amount);
        limitsUpdate.put("available_credit_limitz", limitAmountCredit);

        Map<String, BigDecimal> limitAmountWithdrawal = new LinkedHashMap<>();
        limitAmountWithdrawal.put("amount", amount);
        limitsUpdate.put("available_withdrawal_limitz", limitAmountWithdrawal);

        ObjectMapper mapper = new ObjectMapper();

        given(service.updateLimits(account.getId(), amount, amount)).willReturn(account);
        mvc.perform(patch("/v1/accounts/1")
                .content(mapper.writeValueAsString(limitsUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}

package com.regisan.payments.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.dto.TransactionDTO;
import com.regisan.payments.api.service.TransactionService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService service;

    private TransactionDTO transaction;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        transaction = new TransactionDTO();
        transaction.setAccountId(1L);
        transaction.setAmount(new BigDecimal("1234.5"));
        transaction.setOperationTypeId(OperationType.COMPRA_A_VISTA.getId());
    }

    @Test
    public void testAddTransaction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(post("/v1/transactions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());
    }

    @Test(expected = JsonMappingException.class)
    public void testAddTransactionInvalidOperationTypeId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        transaction.setOperationTypeId(5); // tipo invalido

        mvc.perform(post("/v1/transactions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest());
    }
}

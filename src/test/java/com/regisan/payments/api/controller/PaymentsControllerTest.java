package com.regisan.payments.api.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.dto.PaymentDTO;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PaymentsController.class)
public class PaymentsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService service;

    private List<PaymentDTO> paymentList;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        paymentList = new ArrayList<>(1);

        PaymentDTO p = new PaymentDTO();
        p.setAccountId(1L);
        p.setAmount(new BigDecimal("123.45"));
        paymentList.add(p);
    }

    @Test
    public void testAddPayment() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(post("/v1/payments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentList)))
                .andExpect(status().isCreated());
    }

}

package com.regisan.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDTO {

    @JsonProperty("account_id")
    private Long accountId;

    private BigDecimal amount;
}

package com.regisan.payments.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "Accounts")
public class Account {

    public static final int DUE_DATE = 10; // dia de vencimento da fatura

    @Id
    @Column(name = "account_ID")
    @JsonProperty("account_id")
    private Long id;

    @JsonProperty("available_credit_limit")
    private BigDecimal availableCreditLimit;

    @JsonProperty("available_withdrawal_limit")
    private BigDecimal availableWithdrawalLimit;

    public boolean hasCreditLimit(BigDecimal amount) {
        if (availableCreditLimit.compareTo(amount) == -1)
            return false;

        return true;
    }

    public boolean hasWithdrawalLimit(BigDecimal amount) {
        if (availableWithdrawalLimit.compareTo(amount) == -1)
            return false;

        return true;
    }

}

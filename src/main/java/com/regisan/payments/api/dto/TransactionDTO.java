package com.regisan.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.regisan.payments.api.domain.OperationType;
import com.regisan.payments.api.exception.OperationTypeException;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Data
public class TransactionDTO implements Serializable {

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("operation_type_id")
    private Integer operationTypeId;

    @JsonProperty(value = "installment_plan", required = false)
    private Integer installmentPlan;

    private BigDecimal amount;

    public OperationType getOperationType() {

        OperationType operationType = null;

        switch (this.operationTypeId) {
            case 1 : operationType = OperationType.COMPRA_A_VISTA; break;
            case 2 : operationType = OperationType.COMPRA_PARCELADA; break;
            case 3 : operationType = OperationType.SAQUE; break;
            case 4 : operationType = OperationType.PAGAMENTO; break;
            default: throw new OperationTypeException("Operation Type invalido");
        }

        return operationType;
    }
}

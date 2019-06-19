package com.regisan.payments.api.domain;

import lombok.Getter;

@Getter
public enum  OperationType {

    COMPRA_A_VISTA(1, 2),
    COMPRA_PARCELADA(2, 1),
    SAQUE(3, 0),
    PAGAMENTO(4, 0);

    OperationType(int id, int chargeOrder) {
        this.id = id;
        this.chargeOrder = chargeOrder;
    }

    private final int id;
    private final int chargeOrder;


}

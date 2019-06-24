package com.regisan.payments.api.service;

import com.regisan.payments.api.domain.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionFactory {

    @Autowired
    private CompraAVistaStrategy compraAVistaStrategy;

    @Autowired
    private CompraParceladaStrategy compraParceladaStrategy;

    @Autowired
    private SaqueStrategy saqueStrategy;

    @Autowired
    private PagamentoStrategy pagamentoStrategy;

    public TransactionStrategy getStrategy(OperationType operationType) {

        TransactionStrategy strategy = null;

        switch (operationType) {
            case COMPRA_A_VISTA: {
                strategy = compraAVistaStrategy;
                break;
            }
            case COMPRA_PARCELADA: {
                strategy = compraParceladaStrategy;
                break;
            }
            case SAQUE: {
                strategy = saqueStrategy;
                break;
            }
            case PAGAMENTO: {
                strategy = pagamentoStrategy;
                break;
            }
        }
        return strategy;
    }

}

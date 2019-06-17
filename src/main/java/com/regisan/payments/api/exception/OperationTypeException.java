package com.regisan.payments.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OperationTypeException extends RuntimeException {

    public OperationTypeException(String msg) {
        super(msg);
    }
}

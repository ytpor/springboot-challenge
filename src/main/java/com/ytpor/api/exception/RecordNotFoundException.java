package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3211373322472624175L;

    public RecordNotFoundException(String exception) {
        super(exception);
    }
}

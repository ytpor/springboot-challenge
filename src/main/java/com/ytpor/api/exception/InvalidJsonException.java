package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidJsonException extends RuntimeException {

    private static final long serialVersionUID = -8990191232707545297L;

    public InvalidJsonException(String exception) {
        super(exception);
    }
}
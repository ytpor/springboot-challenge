package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRequestBodyException extends RuntimeException {

    private static final long serialVersionUID = -984344725802246853L;

    public MissingRequestBodyException(String exception) {
        super(exception);
    }
}
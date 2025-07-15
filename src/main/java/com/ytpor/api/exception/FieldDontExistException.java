package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldDontExistException extends RuntimeException {

    private static final long serialVersionUID = -7894615607182379996L;

    public FieldDontExistException(String exception) {
        super(exception);
    }
}
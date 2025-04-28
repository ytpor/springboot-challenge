package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateRecordException extends RuntimeException {

    private static final long serialVersionUID = -4279217446248389504L;

    public DuplicateRecordException(String exception) {
        super(exception);
    }
}
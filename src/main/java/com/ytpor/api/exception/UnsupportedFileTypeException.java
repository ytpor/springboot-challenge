package com.ytpor.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedFileTypeException extends RuntimeException {

    private static final long serialVersionUID = -731690522298779372L;

    public UnsupportedFileTypeException(String exception) {
        super(exception);
    }
}
package com.Bank.Management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedDataException extends RuntimeException {
    private final String field;
    private final String value;

    public DuplicatedDataException(String field, String value) {
        super(String.format("El %s '%s' ya existe y debe ser único.", field, value));
        this.field = field;
        this.value = value;
    }
}
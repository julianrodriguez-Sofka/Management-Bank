package com.Bank.Management.exception;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    private final String entity;
    private final String identifier;

    public DuplicatedDataException(String entity, String identifier) {
        super(String.format("%s con el identificador '%s' ya existe.", entity, identifier));
        this.entity = entity;
        this.identifier = identifier;
    }
}
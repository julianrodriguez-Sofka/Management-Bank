package com.Bank.Management.exception;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(Long id, String entity) {
        super(String.format("%s con ID %d no encontrado.", entity, id));
    }

    public DataNotFoundException(String identifier, String entity) {
        super(String.format("%s con el identificador '%s' no encontrado.", entity, identifier));
    }
}
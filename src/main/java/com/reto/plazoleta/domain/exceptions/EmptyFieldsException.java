package com.reto.plazoleta.domain.exceptions;

public class EmptyFieldsException extends RuntimeException {
    public EmptyFieldsException(String message) {
        super(message);
    }
}

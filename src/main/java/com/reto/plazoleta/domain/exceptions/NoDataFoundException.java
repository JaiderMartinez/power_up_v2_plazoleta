package com.reto.plazoleta.domain.exceptions;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException() {
        super("No content");
    }
}

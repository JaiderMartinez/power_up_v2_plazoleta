package com.reto.plazoleta.domain.exception;

public class OrderInProcessException extends RuntimeException{

    public OrderInProcessException(String message) {
        super(message);
    }
}

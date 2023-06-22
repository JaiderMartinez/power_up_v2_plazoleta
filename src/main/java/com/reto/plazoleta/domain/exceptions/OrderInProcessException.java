package com.reto.plazoleta.domain.exceptions;

public class OrderInProcessException extends RuntimeException{

    public OrderInProcessException(String message) {
        super(message);
    }
}

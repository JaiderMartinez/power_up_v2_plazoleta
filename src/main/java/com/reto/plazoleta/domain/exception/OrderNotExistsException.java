package com.reto.plazoleta.domain.exception;

public class OrderNotExistsException extends RuntimeException{

    public OrderNotExistsException(String message) {
        super(message);
    }
}

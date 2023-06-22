package com.reto.plazoleta.domain.exceptions;

public class OrderNotExistsException extends RuntimeException{

    public OrderNotExistsException(String message) {
        super(message);
    }
}

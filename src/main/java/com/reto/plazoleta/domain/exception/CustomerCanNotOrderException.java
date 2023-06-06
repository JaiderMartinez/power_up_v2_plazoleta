package com.reto.plazoleta.domain.exception;

public class CustomerCanNotOrderException extends RuntimeException{

    public CustomerCanNotOrderException(String message) {
        super(message);
    }
}

package com.reto.plazoleta.domain.exception;

public class CustomerHasAOrderInProcessException extends RuntimeException{

    public CustomerHasAOrderInProcessException(String message) {
        super(message);
    }
}

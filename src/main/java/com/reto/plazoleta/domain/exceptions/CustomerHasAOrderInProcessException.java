package com.reto.plazoleta.domain.exceptions;

public class CustomerHasAOrderInProcessException extends RuntimeException{

    public CustomerHasAOrderInProcessException(String message) {
        super(message);
    }
}

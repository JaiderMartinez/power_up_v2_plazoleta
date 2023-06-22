package com.reto.plazoleta.domain.exceptions;

public class DishNotExistsException extends RuntimeException{
    public DishNotExistsException(String message) {
        super(message);
    }
}

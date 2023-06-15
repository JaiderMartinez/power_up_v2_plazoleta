package com.reto.plazoleta.domain.exception;

public class RestaurantNotExistException extends RuntimeException {

    public RestaurantNotExistException(String message) {
        super(message);
    }
}

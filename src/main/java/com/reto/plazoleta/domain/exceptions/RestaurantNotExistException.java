package com.reto.plazoleta.domain.exceptions;

public class RestaurantNotExistException extends RuntimeException {

    public RestaurantNotExistException(String message) {
        super(message);
    }
}

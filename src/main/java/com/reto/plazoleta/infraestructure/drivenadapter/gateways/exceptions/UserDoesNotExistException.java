package com.reto.plazoleta.infraestructure.drivenadapter.gateways.exceptions;

public class UserDoesNotExistException extends RuntimeException{

    public UserDoesNotExistException(String message) {
        super(message);
    }
}

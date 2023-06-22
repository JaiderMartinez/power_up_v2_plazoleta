package com.reto.plazoleta.infraestructure.drivenadapter.webclients.exceptions;

public class UserDoesNotExistException extends RuntimeException{

    public UserDoesNotExistException(String message) {
        super(message);
    }
}

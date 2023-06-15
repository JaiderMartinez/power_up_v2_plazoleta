package com.reto.plazoleta.infraestructure.configuration.security.jwt.exceptions;

public class AuthenticationFailedException extends RuntimeException{

    public AuthenticationFailedException() {
        super("Failed to parse JWT token");
    }
}

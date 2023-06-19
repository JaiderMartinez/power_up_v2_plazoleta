package com.reto.plazoleta.infraestructure.configuration.security.jwt;

public interface IUserVerifierToken {

    boolean isValidTokenUser(String token);
}

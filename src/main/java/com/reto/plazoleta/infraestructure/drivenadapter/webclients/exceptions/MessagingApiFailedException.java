package com.reto.plazoleta.infraestructure.drivenadapter.webclients.exceptions;

public class MessagingApiFailedException extends RuntimeException{

    public MessagingApiFailedException() {
        super("Something went wrong when sending the request");
    }
}

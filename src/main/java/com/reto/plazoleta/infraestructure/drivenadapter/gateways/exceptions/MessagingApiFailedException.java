package com.reto.plazoleta.infraestructure.drivenadapter.gateways.exceptions;

public class MessagingApiFailedException extends RuntimeException{

    public MessagingApiFailedException() {
        super("Something went wrong when sending the request");
    }
}

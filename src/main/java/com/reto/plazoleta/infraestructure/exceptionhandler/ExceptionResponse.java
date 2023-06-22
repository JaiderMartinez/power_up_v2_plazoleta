package com.reto.plazoleta.infraestructure.exceptionhandler;

public enum ExceptionResponse {

    EMPTY_FIELDS("Fields cannot be empty"),
    INVALID_DATA("Rectify the field format is incorrect"),
    AUTHENTICATION_FAILED("Failed to parse JWT token"),
    TOKEN_INVALID("The token is invalid"),
    USER_DOES_NOT_EXIST("User id not found"),
    ACCESS_DENIED("Access denied to this resource"),
    DISH_NOT_EXISTS("The dish not exist"),
    RESTAURANT_NOT_EXIST("The restaurant not exist"),
    ORDER_IN_PROCESS("The user has an order in process"),
    ORDER_NOT_FOUND("Order not exist"),
    FAILED_IN_MESSAGING_MICROSERVICE("Unexpected failure in the messaging microservice");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
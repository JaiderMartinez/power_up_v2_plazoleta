package com.reto.plazoleta.infraestructure.exceptionhandler;

import com.reto.plazoleta.domain.exceptions.CustomerHasAOrderInProcessException;
import com.reto.plazoleta.domain.exceptions.DishNotExistsException;
import com.reto.plazoleta.domain.exceptions.EmptyFieldsException;
import com.reto.plazoleta.domain.exceptions.InvalidDataException;
import com.reto.plazoleta.domain.exceptions.RestaurantNotExistException;
import com.reto.plazoleta.domain.exceptions.OrderInProcessException;
import com.reto.plazoleta.domain.exceptions.OrderNotExistsException;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.exceptions.AuthenticationFailedException;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.exceptions.MessagingApiFailedException;
import com.reto.plazoleta.infraestructure.drivenadapter.webclients.exceptions.UserDoesNotExistException;
import com.reto.plazoleta.domain.exceptions.NoDataFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Void> handleNoDataFoundException() {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EmptyFieldsException.class)
    public ResponseEntity<Map<String, String>> handleEmptyFieldsException(
            EmptyFieldsException ignoredEmptyFieldsException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.EMPTY_FIELDS.getMessage()));
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDataException(
            InvalidDataException ignoredInvalidDataException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.INVALID_DATA.getMessage()));
    }

    @ExceptionHandler(RestaurantNotExistException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantNotExistException(
            RestaurantNotExistException ignoredRestaurantNotExistException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.RESTAURANT_NOT_EXIST.getMessage()));
    }

    @ExceptionHandler(DishNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleDishNotExistsException(
            DishNotExistsException dishNotExistsException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.DISH_NOT_EXISTS.getMessage()));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationFailedException(
            AuthenticationFailedException ignoredAuthenticationFailedException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.AUTHENTICATION_FAILED.getMessage()));
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<Map<String, String>> handleUserDoesNotExistException(
            UserDoesNotExistException ignoredUserDoesNotExistException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.USER_DOES_NOT_EXIST.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(
            AccessDeniedException ignoredAccessDeniedException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.ACCESS_DENIED.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException authenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(MESSAGE, authenticationException.getMessage()));
    }

    @ExceptionHandler(CustomerHasAOrderInProcessException.class)
    public ResponseEntity<Map<String, String>> handleCustomerCanNotOrderException(
            CustomerHasAOrderInProcessException ignoredCustomerCanNotOrderException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.ORDER_IN_PROCESS.getMessage()));
    }

    @ExceptionHandler(OrderNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotExistsException(
            OrderNotExistsException ignoredOrderNotExistsException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.ORDER_NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(OrderInProcessException.class)
    public ResponseEntity<Map<String, String>> handleOrderInProcessException(
            OrderInProcessException orderInProcessException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap(MESSAGE, orderInProcessException.getMessage()));
    }

    @ExceptionHandler(MessagingApiFailedException.class)
    public ResponseEntity<Map<String, String>> handleMessagingApiFailedException(
            MessagingApiFailedException ignoredMessagingApiFailedException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.FAILED_IN_MESSAGING_MICROSERVICE.getMessage()));
    }
}

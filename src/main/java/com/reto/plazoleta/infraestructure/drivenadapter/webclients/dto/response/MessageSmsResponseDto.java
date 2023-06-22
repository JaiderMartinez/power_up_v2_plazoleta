package com.reto.plazoleta.infraestructure.drivenadapter.webclients.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MessageSmsResponseDto {

    private String status;
    private String errorCode;
    private String errorMessage;
    private String priceMessage;
    private String direction;
    private LocalDate dateCreated;
    private LocalDate dateSent;
    private LocalDate dateUpdated;
}

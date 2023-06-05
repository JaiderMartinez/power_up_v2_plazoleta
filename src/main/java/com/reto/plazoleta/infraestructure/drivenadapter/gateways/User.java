package com.reto.plazoleta.infraestructure.drivenadapter.gateways;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private Long idUser;
    private String name;
    private String lastName;
    private Long identificationDocument;
    private String cellPhone;
    private String email;
    private String password;
    private String rol;
}

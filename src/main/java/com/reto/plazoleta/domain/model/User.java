package com.reto.plazoleta.domain.model;

public class User {

    private Long idUser;
    private String name;
    private String lastName;
    private Long identificationDocument;
    private String cellPhone;
    private String email;
    private String rol;

    public User() {
    }

    public User(Long idUser, String name, String lastName, Long identificationDocument, String cellPhone, String email, String rol) {
        this.idUser = idUser;
        this.name = name;
        this.lastName = lastName;
        this.identificationDocument = identificationDocument;
        this.cellPhone = cellPhone;
        this.email = email;
        this.rol = rol;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getIdentificationDocument() {
        return identificationDocument;
    }

    public void setIdentificationDocument(Long identificationDocument) {
        this.identificationDocument = identificationDocument;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

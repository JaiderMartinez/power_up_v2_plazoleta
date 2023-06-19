package com.reto.plazoleta.domain.model;

public class RestaurantModel {

    private Long idRestaurant;
    private String name;
    private String address;
    private String phone;
    private String urlLogo;
    private Long nit;
    private Long idOwner;

    public RestaurantModel() {}

    public RestaurantModel(Long idRestaurant, String name, String address, String phone, String urlLogo, Long nit, Long idOwner) {
        this.idRestaurant = idRestaurant;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.urlLogo = urlLogo;
        this.nit = nit;
        this.idOwner = idOwner;
    }
    public Long getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(Long idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public Long getNit() {
        return nit;
    }

    public void setNit(Long nit) {
        this.nit = nit;
    }

    public Long getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(Long idOwner) {
        this.idOwner = idOwner;
    }
}

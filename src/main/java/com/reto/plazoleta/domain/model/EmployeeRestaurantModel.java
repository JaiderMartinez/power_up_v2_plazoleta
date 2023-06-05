package com.reto.plazoleta.domain.model;

public class EmployeeRestaurantModel {

    private Long idRestaurantEmployee;
    private Long idUserEmployee;
    private Long idRestaurant;

    public EmployeeRestaurantModel() {
    }

    public EmployeeRestaurantModel(Long idRestaurantEmployee, Long idUserEmployee, Long idRestaurant) {
        this.idRestaurantEmployee = idRestaurantEmployee;
        this.idUserEmployee = idUserEmployee;
        this.idRestaurant = idRestaurant;
    }

    public Long getIdRestaurantEmployee() {
        return idRestaurantEmployee;
    }

    public void setIdRestaurantEmployee(Long idRestaurantEmployee) {
        this.idRestaurantEmployee = idRestaurantEmployee;
    }

    public Long getIdUserEmployee() {
        return idUserEmployee;
    }

    public void setIdUserEmployee(Long idUserEmployee) {
        this.idUserEmployee = idUserEmployee;
    }

    public Long getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(Long idRestaurant) {
        this.idRestaurant = idRestaurant;
    }
}

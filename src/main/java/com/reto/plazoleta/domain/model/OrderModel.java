package com.reto.plazoleta.domain.model;

import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;

import java.time.LocalDate;

public class OrderModel {

    private Long idOrder;
    private Long idUserCustomer;
    private LocalDate date;
    private StatusOrder status;
    private EmployeeRestaurantModel employeeRestaurantModel;
    private RestaurantModel restaurantModel;

    public OrderModel() {
    }

    public OrderModel(Long idOrder, Long idUserCustomer, LocalDate date, StatusOrder status,
                      EmployeeRestaurantModel employeeRestaurantModel, RestaurantModel restaurantModel) {
        this.idOrder = idOrder;
        this.idUserCustomer = idUserCustomer;
        this.date = date;
        this.status = status;
        this.employeeRestaurantModel = employeeRestaurantModel;
        this.restaurantModel = restaurantModel;
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Long getIdUserCustomer() {
        return idUserCustomer;
    }

    public void setIdUserCustomer(Long idUserCustomer) {
        this.idUserCustomer = idUserCustomer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public StatusOrder getStatus() {
        return status;
    }

    public void setStatus(StatusOrder status) {
        this.status = status;
    }

    public EmployeeRestaurantModel getEmployeeRestaurantModel() {
        return employeeRestaurantModel;
    }

    public void setEmployeeRestaurantModel(EmployeeRestaurantModel employeeRestaurantModel) {
        this.employeeRestaurantModel = employeeRestaurantModel;
    }

    public RestaurantModel getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }
}

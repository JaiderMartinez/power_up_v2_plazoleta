package com.reto.plazoleta.domain.model;

public class OrderDishModel {

    private Long idOrderDish;
    private OrderModel orderModel;
    private DishModel dishModel;
    private Integer amount;

    public OrderDishModel() {
    }

    public OrderDishModel(Long idOrdersDishes, OrderModel orderModel, DishModel dishModel, Integer amount) {
        this.idOrderDish = idOrdersDishes;
        this.orderModel = orderModel;
        this.dishModel = dishModel;
        this.amount = amount;
    }

    public Long getIdOrderDish() {
        return idOrderDish;
    }

    public void setIdOrderDish(Long idOrderDish) {
        this.idOrderDish = idOrderDish;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public DishModel getDishModel() {
        return dishModel;
    }

    public void setDishModel(DishModel dishModel) {
        this.dishModel = dishModel;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}

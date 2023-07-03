package com.reto.plazoleta.domain.model;

import com.reto.plazoleta.domain.model.dishes.DishModel;

public class OrderDishModel {

    private Long idOrderDish;
    private OrderModel orderModel;
    private DishModel dishModel;
    private Integer amount;
    private Integer gramsDish;
    private String sideDish;
    private String flavor;

    public OrderDishModel() {
    }

    public OrderDishModel(Long idOrdersDishes, OrderModel orderModel, DishModel dishModel, Integer amount) {
        this.idOrderDish = idOrdersDishes;
        this.orderModel = orderModel;
        this.dishModel = dishModel;
        this.amount = amount;
    }

    public OrderDishModel(Long idOrderDish, OrderModel orderModel, DishModel dishModel, Integer amount,
                          Integer gramsDish, String sideDish, String flavor) {
        this.idOrderDish = idOrderDish;
        this.orderModel = orderModel;
        this.dishModel = dishModel;
        this.amount = amount;
        this.gramsDish = gramsDish;
        this.sideDish = sideDish;
        this.flavor = flavor;
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

    public Integer getGramsDish() {
        return gramsDish;
    }

    public void setGramsDish(Integer gramsDish) {
        this.gramsDish = gramsDish;
    }

    public String getSideDish() {
        return sideDish;
    }

    public void setSideDish(String sideDish) {
        this.sideDish = sideDish;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }
}

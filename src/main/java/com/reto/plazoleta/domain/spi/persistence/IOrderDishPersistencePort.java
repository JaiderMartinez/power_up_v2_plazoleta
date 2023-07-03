package com.reto.plazoleta.domain.spi.persistence;

import com.reto.plazoleta.domain.model.orders.OrderDishModel;

import java.util.List;

public interface IOrderDishPersistencePort {

    List<OrderDishModel> saveAllOrdersDishes(List<OrderDishModel> orderDishModelList);
}

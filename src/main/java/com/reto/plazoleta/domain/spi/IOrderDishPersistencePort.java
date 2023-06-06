package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.OrderDishModel;

import java.util.List;

public interface IOrderDishPersistencePort {

    void saveAllOrdersDishes(List<OrderDishModel> orderDishModelList);
}

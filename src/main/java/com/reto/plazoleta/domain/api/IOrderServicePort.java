package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.OrderDishModel;
import com.reto.plazoleta.domain.model.OrderModel;

import java.util.List;

public interface IOrderServicePort {

    OrderModel saveOrder(OrderModel orderModelRequest, List<OrderDishModel> listOrderDishModelWithValueOfNameFromDishAndAmountOfDishes, String tokenWithPrefixBearer);
}

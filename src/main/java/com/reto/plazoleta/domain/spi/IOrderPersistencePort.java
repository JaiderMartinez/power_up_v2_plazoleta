package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.OrderModel;

import java.util.List;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);

    List<OrderModel> findByIdUserCustomerAndIdRestaurant(Long idUser, Long idRestaurant);
}
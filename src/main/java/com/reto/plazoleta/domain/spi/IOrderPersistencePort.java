package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.OrderModel;
import com.reto.plazoleta.infraestructure.drivenadapter.entity.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderPersistencePort {

    OrderModel saveOrder(OrderModel orderModel);

    List<OrderModel> findByIdUserCustomerAndIdRestaurant(Long idUser, Long idRestaurant);

    Page<OrderModel> findAllByRestaurantEntityIdRestaurantAndStatusOrder(Pageable pageable, Long idRestaurant, StatusOrder status);

    OrderModel findByIdOrder(Long idOrder);
}
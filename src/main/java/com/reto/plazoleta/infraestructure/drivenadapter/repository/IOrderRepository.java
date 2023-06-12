package com.reto.plazoleta.infraestructure.drivenadapter.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByIdUserCustomerAndRestaurantEntityIdRestaurant(Long userCustomerId, Long restaurantId);
}

package com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderEntity;
import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByIdUserCustomerAndRestaurantEntityIdRestaurant(Long userCustomerId, Long restaurantId);

    Page<OrderEntity> findAllByRestaurantEntityIdRestaurantAndStatus(Pageable pageable, Long idRestaurant, StatusOrder status);
}

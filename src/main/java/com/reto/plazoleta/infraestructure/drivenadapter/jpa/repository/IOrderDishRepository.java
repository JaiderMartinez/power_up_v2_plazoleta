package com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.OrderDishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderDishRepository extends JpaRepository<OrderDishEntity, Long> {
}

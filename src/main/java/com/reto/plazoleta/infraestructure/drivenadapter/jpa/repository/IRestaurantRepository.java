package com.reto.plazoleta.infraestructure.drivenadapter.jpa.repository;

import com.reto.plazoleta.infraestructure.drivenadapter.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    Page<RestaurantEntity> findAllByOrderByName(Pageable pageable);
}
